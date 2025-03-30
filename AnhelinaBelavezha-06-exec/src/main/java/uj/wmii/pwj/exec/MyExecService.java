package uj.wmii.pwj.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class MyExecService implements ExecutorService {
    private boolean isShutdown = false;
    private final List<Thread> workerThreads = new ArrayList<>();
    private final BlockingQueue<Runnable> tasksQueue = new LinkedBlockingQueue<>();

    static MyExecService newInstance() {
        return new MyExecService();
    }

    @Override
    public void shutdown() {
        isShutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        for (Thread workerThread : workerThreads) {
            workerThread.interrupt();
        }
        List<Runnable> remainingTasks = new ArrayList<>();
        tasksQueue.drainTo(remainingTasks);
        return remainingTasks;
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown && workerThreads.stream().noneMatch(Thread::isAlive);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        while (!isTerminated() && System.nanoTime() < deadline) {
            Thread.sleep(10);
        }
        return isTerminated();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        FutureTask<T> futureTask = new FutureTask<>(task);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        execute(futureTask);
        return futureTask;
    }

    @Override
    public Future<?> submit(Runnable task) {
        return submit(task, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        if (tasks == null) throw new NullPointerException();
        List<Future<T>> futures = new ArrayList<>();
        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }
        for (Future<T> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ignored) {
            }
        }
        return futures;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (tasks == null || unit == null) throw new NullPointerException();
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        List<Future<T>> futures = new ArrayList<>();
        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }
        for (Future<T> future : futures) {
            try {
                long timeLeft = deadline - System.nanoTime();
                if (timeLeft <= 0) break;
                future.get(timeLeft, TimeUnit.NANOSECONDS);
            } catch (ExecutionException | TimeoutException ignored) {
            }
        }
        return futures;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        List<Future<T>> listFuture = new ArrayList<>();
        for (Callable<T> task : tasks) {
            listFuture.add(submit(task));
        }
        for (Future<T> future : listFuture) {
            try {
                return future.get();
            } catch (Exception e) {
                future.cancel(true);
            }
        }
        throw new ExecutionException(null);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        List<Future<T>> listFuture = new ArrayList<>();
        for (Callable<T> task : tasks) {
            listFuture.add(submit(task));
        }
        for (Future<T> future : listFuture) {
            try {
                return future.get(timeout, unit);
            } catch (TimeoutException e) {
                future.cancel(true);
            }
        }
        throw new ExecutionException("", new Exception());
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) throw new NullPointerException();
        if (isShutdown) {
            throw new RejectedExecutionException();
        } else {
            var thread = new Thread(() -> {
                command.run();
                workerThreads.remove(Thread.currentThread());
            });
            workerThreads.add(thread);
            thread.start();
        }
    }
}