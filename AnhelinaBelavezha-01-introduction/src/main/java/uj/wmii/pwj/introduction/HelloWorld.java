package uj.wmii.pwj.introduction;

public class HelloWorld {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print("No input parameters provided\n");
        } else {
            for (String arg : args) {
                System.out.print(arg + "\n");
            }
        }
    }
}