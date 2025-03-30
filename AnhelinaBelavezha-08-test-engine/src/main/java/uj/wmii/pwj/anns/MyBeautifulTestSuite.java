package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {

    @MyTest
    public void testSomething() {
        System.out.println("I'm testing something!");
    }

    @MyTest(params = {"a param", "b param", "c param. Long, long C param."})
    public void testWithParam(String param1, String param2, String param3) {
        System.out.printf("I was invoked with parameters: %s, %s, %s\n", param1, param2, param3);
    }

    public void notATest() {
        System.out.println("I'm not a test.");
    }

    @MyTest
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @MyTest(params = {"2", "3"}, expected = {"5"})
    public int add(int a, int b) {
        return a + b;
    }

    @MyTest
    public void shouldThrowException() {
        throw new IllegalArgumentException("This is a test exception.");
    }

    @MyTest(params = {"hello"}, expected = {"HELLO"})
    public String toUpperCase(String input) {
        if (input == null) return null;
        return input.toUpperCase();
    }

    @MyTest(params = {"WORLD"}, expected = {"world"})
    public String toLowerCase(String input) {
        if (input == null) return null;
        return input.toLowerCase();
    }

    @MyTest(params = {"This is a test"}, expected = {"4"})
    public int countWords(String input) {
        if (input == null || input.isEmpty()) return 0;
        return input.split("\\s+").length;
    }

    @MyTest(params = {"4"}, expected = {"Even"})
    public String isEven(int number) {
        return (number % 2 == 0) ? "Even" : "Odd";
    }

    @MyTest(params = {"hello"}, expected = {"HELLO_FAIL"})
    public String toUpperCaseFail(String input) {
        if (input == null) return null;
        return input.toUpperCase();
    }

    @MyTest(params = {"2"}, expected = {"5"})
    public int incorrectAddition(int number) {
        return number + 2;
    }
}
