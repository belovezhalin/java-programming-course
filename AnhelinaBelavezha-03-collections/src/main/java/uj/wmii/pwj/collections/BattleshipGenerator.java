package uj.wmii.pwj.collections;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public interface BattleshipGenerator {

    String generateMap();

    static BattleshipGenerator defaultInstance() {
        return new BattleshipMapCreation();
    }

}

class BattleshipMapCreation implements BattleshipGenerator {
    private final int SIZE = 10;
    private final int[][] board = new int[SIZE][SIZE];
    private int row, col;
    private int verticalShift, horizontalShift;
    private final Random random = new Random();

    static boolean withinBoardLimits(int[][] board, int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[0].length;
    }

    public void pickStartingPoint() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }
        int[] chosenCell = emptyCells.get(random.nextInt(emptyCells.size()));
        row = chosenCell[0];
        col = chosenCell[1];
    }

    public void determineDirection() {
        int orientation = random.nextInt(2);
        verticalShift = 0;
        horizontalShift = 0;

        if (orientation == 0) verticalShift = random.nextInt(3) - 1;
        else horizontalShift = random.nextInt(3) - 1;
    }

    public void markBoundary() {
        int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    for (int k = 0; k < 8; k++) {
                        int boundaryRow = i + rowOffsets[k];
                        int boundaryCol = j + colOffsets[k];
                        if (withinBoardLimits(board, boundaryRow, boundaryCol) && board[boundaryRow][boundaryCol] == 0) {
                            board[boundaryRow][boundaryCol] = 2;
                        }
                    }
                }
            }
        }
    }

    public void create4CellShip() {
        row = random.nextInt(SIZE);
        col = random.nextInt(SIZE);
        board[row][col] = 1;
        int cellsRemaining = 3;

        while(cellsRemaining > 0) {
            determineDirection();
            if (withinBoardLimits(board, row + verticalShift, col + horizontalShift) && board[row + verticalShift][col + horizontalShift] == 0) {
                row += verticalShift;
                col += horizontalShift;
                board[row][col] = 1;
                cellsRemaining--;
            }
        }
    }

    public void create3CellShip(){
        int attemptsRemaining = 3;
        int retries = 0;

        while (attemptsRemaining == 3) {
            pickStartingPoint();
            int startRow = row, startCol = col;
            board[startRow][startCol] = 1;
            attemptsRemaining = 2;

            while (attemptsRemaining == 2) {
                determineDirection();
                retries++;
                int nextRow, nextCol;

                if (withinBoardLimits(board, startRow + verticalShift, startCol + horizontalShift) && board[startRow + verticalShift][startCol + horizontalShift] == 0) {
                    nextRow = startRow + verticalShift;
                    nextCol = startCol + horizontalShift;
                    board[nextRow][nextCol] = 1;
                    attemptsRemaining = 1;
                    retries = 0;

                    while (attemptsRemaining == 1) {
                        determineDirection();
                        retries++;

                        if (withinBoardLimits(board, nextRow + verticalShift, nextCol + horizontalShift) && board[nextRow + verticalShift][nextCol + horizontalShift] == 0) {
                            row = nextRow + verticalShift;
                            col = nextCol + horizontalShift;
                            board[row][col] = 1;
                            attemptsRemaining = 0;
                        } else if (retries >= 4) {
                            board[nextRow][nextCol] = 0;
                            attemptsRemaining = 2;
                            retries = 0;
                            break;
                        }
                    }
                } else if (retries >= 4) {
                    board[startRow][startCol] = 0;
                    attemptsRemaining = 3;
                    retries = 0;
                    break;
                }
            }
        }
    }

    public void create2CellShip() {
        pickStartingPoint();
        int attemptsRemaining = 1;
        int startRow = row, startCol = col;
        board[startRow][startCol] = 1;
        int retries = 0;

        while(attemptsRemaining == 1) {
            retries++;
            determineDirection();

            if (withinBoardLimits(board, startRow + verticalShift, startCol + horizontalShift) && board[startRow + verticalShift][startCol + horizontalShift] == 0) {
                row = startRow + verticalShift;
                col = startCol + horizontalShift;
                board[startRow][startCol] = 1;
                board[row][col] = 1;
                attemptsRemaining = 0;
            } else if(retries >= 4) {
                board[row][col] = 0;
                board[startRow][startCol] = 0;
                pickStartingPoint();
                startRow = row;
                startCol = col;
                retries = 0;
                board[startRow][startCol] = 1;
            }
        }
    }

    public void create1CellShip() {
        pickStartingPoint();
        board[row][col] = 1;
    }

    public String generateMap() {
        StringBuilder map = new StringBuilder();

        create4CellShip(); //only 1 4-cell ship
        markBoundary();

        for (int i = 0; i < 2; i++) {
            create3CellShip(); //only 2 3-cell ship
            markBoundary();
        }

        for (int i = 0; i < 3; i++) {
            create2CellShip(); //only 3 2-cell ship
            markBoundary();
        }

        for (int i = 0; i < 4; i++) {
            create1CellShip(); //only 4 1-cell ship
            if(i != 3) {
                markBoundary();
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                map.append(board[i][j] == 1 ? "#" : ".");
            }
        }
        return map.toString();
    }
}
