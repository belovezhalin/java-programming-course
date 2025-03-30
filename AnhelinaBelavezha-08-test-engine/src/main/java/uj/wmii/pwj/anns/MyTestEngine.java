package uj.wmii.pwj.anns;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTestEngine {

    private final String className;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please specify test class name");
            System.exit(-1);
        }
        String className = args[0].trim();
        System.out.println("---------------------------------------------------------");
        System.out.printf("Testing class: %s\n", className);
        MyTestEngine engine = new MyTestEngine(className);
        engine.runTests();
        System.out.println("---------------------------------------------------------");
    }

    public MyTestEngine(String className) {
        this.className = className;
    }

    public void runTests() {
        printBanner();
        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);
        int totalTests = testMethods.size();
        int passCount = 0;
        int failCount = 0;
        int errorCount = 0;
        System.out.println("\u001B[3mRunning tests...\u001B[0m");
        for (int i = 0; i < testMethods.size(); i++) {
            Method method = testMethods.get(i);
            System.out.printf("[%d/%d] Running test: %s\n", i + 1, totalTests, method.getName());
            TestResult result = executeTest(method, unit);
            switch (result) {
                case PASS -> passCount++;
                case FAIL -> failCount++;
                case ERROR -> errorCount++;
            }
        }
        System.out.printf("\nTotal tests: %d | Passed: %d | Failed: %d | Errors: %d\n",
                totalTests, passCount, failCount, errorCount);
    }

    private TestResult executeTest(Method method, Object unit) {
        MyTest annotation = method.getAnnotation(MyTest.class);
        try {
            Object result;
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] params = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == String.class) {
                    params[i] = annotation.params().length > i ? annotation.params()[i] : null;
                } else if (parameterTypes[i] == int.class) {
                    params[i] = annotation.params().length > i ? Integer.parseInt(annotation.params()[i]) : 0;
                } else {
                    params[i] = null;
                }
            }

            result = method.invoke(unit, params);

            if (annotation.expected().length > 0) {
                if (!Arrays.asList(annotation.expected()).contains(result.toString())) {
                    System.out.printf("\u001B[31mTest FAILED.\u001B[0m Expected: %s, Got: %s\n",
                            Arrays.toString(annotation.expected()), result);
                    return TestResult.FAIL;
                }
            }

            System.out.println("\u001B[32mTest PASSED.\u001B[0m");
            return TestResult.PASS;

        } catch (Throwable e) {
            if (annotation.expected().length > 0 && annotation.expected()[0].equals(e.getClass().getName())) {
                System.out.println("\u001B[34mTest PASSED.\u001B[0m Expected exception thrown: " + e);
                return TestResult.PASS;
            }
            System.out.printf("\u001B[33mTest ERROR.\u001B[0m Unexpected exception: %s\n", e);
            return TestResult.ERROR;
        }
    }

    private static List<Method> getTestMethods(Object unit) {
        return Arrays.stream(unit.getClass().getDeclaredMethods())
                .filter(m -> m.getAnnotation(MyTest.class) != null)
                .collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate test class.", e);
        }
    }

    private void printBanner() {
        System.out.println("   _______        _     ______             _             ");
        System.out.println("  |__   __|      | |   |  ____|           (_)            ");
        System.out.println("     | | ___  ___| |_  | |__   _ __   __ _ _ _ __   ___  ");
        System.out.println("     | |/ _ \\/ __| __| |  __| | '_ \\ / _` | | '_ \\ / _ |");
        System.out.println("     | |  __/\\__ \\ |_  | |____| | | | (_| | | | | |  __/ ");
        System.out.println("     |_|\\___||___/\\__| |______|_| |_|\\__, |_|_| |_|\\___|");
        System.out.println("                                       __/ |             ");
        System.out.println("                                      |___/              ");
        System.out.println("Welcome to TestEngine!\n");
    }
}
