package uj.wmii.pwj.collections;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Stack;

public interface Brainfuck {

    /**
     * Executes uploaded program.
     */
    void execute();

    /**
     * Creates a new instance of Brainfuck interpreter with given program, using standard IO and stack of 1024 size.
     * @param program brainfuck program to interpret
     * @return new instance of the interpreter
     * @throws IllegalArgumentException if program is null or empty
     */
    static Brainfuck createInstance(String program) {
        return createInstance(program, System.out, System.in, 1024);
    }

    /**
     * Creates a new instance of Brainfuck interpreter with given parameters.
     * @param program brainfuck program to interpret
     * @param out output stream to be used by interpreter implementation
     * @param in input stream to be used by interpreter implementation
     * @param stackSize maximum stack size, that is allowed for this interpreter
     * @return new instance of the interpreter
     * @throws IllegalArgumentException if: program is null or empty, OR out is null, OR in is null, OR stackSize is below 1.
     */
    static Brainfuck createInstance(String program, PrintStream out, InputStream in, int stackSize) {
        return new BFImplementation(program, out, in, stackSize);
    }

}

class BFImplementation implements Brainfuck {
    Stack<Integer> stack = new Stack<>();
    private int pointerData;
    private final String program;
    private final PrintStream out;
    private final InputStream in;
    private final byte[] cell;

    BFImplementation(String program, PrintStream out, InputStream in, int stackSize) {
        if(program == null) throw new IllegalArgumentException("program is null");
        if(program.isEmpty()) throw new IllegalArgumentException("program is empty");
        if(out == null) throw new IllegalArgumentException("out is null");
        if(in == null) throw new IllegalArgumentException("in is null");
        if(stackSize < 1) throw new IllegalArgumentException("stackSize is < 1");

        this.program = program;
        this.out = out;
        this.in = in;
        this.cell = new byte[stackSize];
    }

    private void printData() {
        out.print((char) cell[pointerData]);
    }

    private void takeData() {
        try {
            int input = in.read();
            if (input != -1) {
                cell[pointerData] = (byte) input;
            }
        } catch (Exception exception) {
            throw new RuntimeException("Error reading input", exception);
        }
    }

    @Override
    public void execute() {
        int instructionPointer = 0;
        while(instructionPointer < program.length()) {
            char currentCommand = program.charAt(instructionPointer);

            switch (currentCommand) {
                case '>' -> pointerData++;
                case '<' -> pointerData--;
                case '+' -> cell[pointerData]++;
                case '-' -> cell[pointerData]--;
                case '.' -> printData();
                case ',' -> takeData();
                case '[' -> {
                    if(cell[pointerData] == 0) {
                        int i = 1;
                        while(i > 0) {
                            instructionPointer++;
                            if(program.charAt(instructionPointer) == '[') i++;
                            if(program.charAt(instructionPointer) == ']') i--;
                        }
                    } else {
                        stack.push(instructionPointer);
                    }
                }
                case ']' -> {
                    if(cell[pointerData] != 0) {
                        instructionPointer = stack.peek();
                    }
                    else {
                        stack.pop();
                    }
                }
                default -> {}
            }
            instructionPointer++;
        }
    }
}