package uj.wmii.pwj.spreadsheet;

public class Spreadsheet {
    private enum Operation {
        ADD {
            @Override
            public int apply(int a, int b) {
                return a + b;
            }
        },
        SUB {
            @Override
            public int apply(int a, int b) {
                return a - b;
            }
        },
        MUL {
            @Override
            public int apply(int a, int b) {
                return a * b;
            }
        },
        DIV {
            @Override
            public int apply(int a, int b) {
                return a / b;
            }
        },
        MOD {
            @Override
            public int apply(int a, int b) {
                return a % b;
            }
        };
        public abstract int apply(int a, int b);

        public static Operation fromString(String operation) {
            return Operation.valueOf(operation.toUpperCase());
        }
    }

    public String[][] calculate(String[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        String[][] result = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = String.valueOf(evaluateCell(input, i, j));
            }
        }
        return result;
    }

    private int evaluateCell(String[][] sheet, int row, int col) {
        String cell = sheet[row][col];

        if (isNumeric(cell)) {
            return Integer.parseInt(cell);
        }

        if (cell.startsWith("$")) {
            int[] coords = parseReference(cell);
            return evaluateCell(sheet, coords[0], coords[1]);
        }

        if (cell.startsWith("=")) {
            return evaluateFormula(sheet, cell);
        }
        return 0;
    }

    private int evaluateFormula(String[][] sheet, String formula) {
        String inner = formula.substring(1);
        String[] parts = inner.split("[(),]");
        String operationName = parts[0].trim();
        String param1 = parts[1].trim();
        String param2 = parts[2].trim();

        int value1 = evaluateParameter(sheet, param1);
        int value2 = evaluateParameter(sheet, param2);

        Operation operation = Operation.fromString(operationName);
        return operation.apply(value1, value2);
    }

    private int evaluateParameter(String[][] sheet, String param) {
        if (isNumeric(param)) {
            return Integer.parseInt(param);
        } else if (param.startsWith("$")) {
            int[] coords = parseReference(param);
            return evaluateCell(sheet, coords[0], coords[1]);
        } else {
            return 0;
        }
    }

    private int[] parseReference(String reference) {
        reference = reference.substring(1);
        char column = reference.charAt(0);
        int row = Integer.parseInt(reference.substring(1)) - 1;
        int col = column - 'A';
        return new int[]{row, col};
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
