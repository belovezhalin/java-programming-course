## Custom Implementation of `ExecutorService`

The `MyExecutorService` class must be implemented to conform to the `ExecutorService` API.

The implementation can be based on a single-thread executor. Existing implementations should not be used; instead, the solution must rely solely on low-level thread APIs! However, existing implementations of the `Future` interface, such as `CompletableFuture`, may be used.

Additionally, tests must be provided to verify all methods. Several example tests are already implemented in the `ExecServiceTest` class, which can be used as a reference.

### NOTE
In this task, simply passing the pipeline is not enough—code analysis is required.
