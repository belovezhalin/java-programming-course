package uj.wmii.pwj.exec;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ExecServiceTest {

    @Test
    void testExecute() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.execute(r);
        doSleep(10);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnable() {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        s.submit(r);
        doSleep(10);
        assertTrue(r.wasRun);
    }

    @Test
    void testScheduleRunnableWithResult() throws Exception {
        MyExecService s = MyExecService.newInstance();
        TestRunnable r = new TestRunnable();
        Object expected = new Object();
        Future<Object> f = s.submit(r, expected);
        doSleep(10);
        assertTrue(r.wasRun);
        assertTrue(f.isDone());
        assertEquals(expected, f.get());
    }

    @Test
    void testScheduleCallable() throws Exception {
        MyExecService s = MyExecService.newInstance();
        StringCallable c = new StringCallable("X", 10);
        Future<String> f = s.submit(c);
        doSleep(20);
        assertTrue(f.isDone());
        assertEquals("X", f.get());
    }

    @Test
    void testShutdown() {
        ExecutorService s = MyExecService.newInstance();
        s.execute(new TestRunnable());
        doSleep(10);
        s.shutdown();
        assertThrows(
            RejectedExecutionException.class,
            () -> s.submit(new TestRunnable()));
    }

    @Test
    void testIsShutdown() {
        MyExecService s = MyExecService.newInstance();
        assertFalse(s.isShutdown());
        s.shutdown();
        assertTrue(s.isShutdown());
    }

    @Test
    void testIsTerminated() {
        MyExecService s = MyExecService.newInstance();
        assertFalse(s.isTerminated());
        s.shutdown();
        doSleep(10);
        assertTrue(s.isTerminated());
    }

    @Test
    void testInvokeAll() throws InterruptedException, ExecutionException {
        MyExecService s = MyExecService.newInstance();
        var jobs = List.of(
                new StringCallable("job1", 10),
                new StringCallable("job2", 200),
                new StringCallable("job3", 500)
        );
        var results = s.invokeAll(jobs);
        assertTrue(results.get(0).isDone());
        assertTrue(results.get(1).isDone());
        assertTrue(results.get(2).isDone());
        assertEquals("job1", results.get(0).get());
        assertEquals("job2", results.get(1).get());
        assertEquals("job3", results.get(2).get());
    }

    @Test
    void testInvokeAllWithTimeout() throws InterruptedException {
        MyExecService s = MyExecService.newInstance();
        var jobs = List.of(
                new StringCallable("job1", 10),
                new StringCallable("job2", 200),
                new StringCallable("job3", 500)
        );
        List<Future<String>> results = s.invokeAll(jobs, 30, TimeUnit.MILLISECONDS);
        assertEquals(3, results.size());
        assertTrue(results.get(0).isDone());
        assertFalse(results.get(1).isDone());
        assertFalse(results.get(2).isDone());
    }

    @Test
    void testInvokeAny() throws ExecutionException, InterruptedException {
        MyExecService s = MyExecService.newInstance();
        var jobs = List.of(
                new StringCallable("job1", 10),
                new StringCallable("job2", 200),
                new StringCallable("job3", 500)
        );
        String result = s.invokeAny(jobs);
        doSleep(50);
        assertEquals("job1", result);
    }

    @Test
    void testInvokeAnyWithTimeout() throws ExecutionException, InterruptedException, TimeoutException {
        MyExecService s = MyExecService.newInstance();
        var jobs = List.of(
                new StringCallable("job1", 25),
                new StringCallable("job2", 200),
                new StringCallable("job3", 1200)
        );

        String result = s.invokeAny(jobs, 100, TimeUnit.MILLISECONDS);
        assertEquals("job1", result);
    }

    static void doSleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

class StringCallable implements Callable<String> {

    private final String result;
    private final int milis;

    StringCallable(String result, int milis) {
        this.result = result;
        this.milis = milis;
    }

    @Override
    public String call() throws Exception {
        ExecServiceTest.doSleep(milis);
        return result;
    }
}
class TestRunnable implements Runnable {

    boolean wasRun;
    @Override
    public void run() {
        wasRun = true;
    }
}
