public class BattleMap {
    int rows;
    int cols;
    char[][] stringMap;

    public BattleMap(int _rows, int _cols, String strMap) {
        rows = _rows;
        cols = _cols;
        stringMap = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                stringMap[i][j] = strMap.charAt(i * cols + j);
    }

    public BattleMap(int _rows, int _cols) {
        rows = _rows;
        cols = _cols;
        stringMap = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                stringMap[i][j] = '?';
    }

    public int[][] getBoard() {
        int[][] board = new int[rows][cols];
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                board[i][j] = stringMap[i][j] == '#' ? 1 : 0;
        return board;
    }

    public void printBoard() {
        System.out.print("  ");
        for (int j = 0; j < cols; j++) System.out.print(" " + j);
        System.out.println();
        for (int i = 0; i < rows; i++) {
            char rowLabel = (char) ('A' + i);
            System.out.print(" " + rowLabel + "  ");
            for (int j = 0; j < cols; j++) {
                System.out.print(stringMap[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void replaceCharacter(int row, int col, char character) {
        stringMap[row][col] = character;
    }

    public boolean isCharacterOnMap(char character) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (stringMap[i][j] == character)
                    return true;
        return false;
    }

    public boolean isOnMap(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isCharacter(int row, int col, char character) {
        if (!isOnMap(row, col)) return false;
        return stringMap[row][col] == character;
    }

    boolean isCharacterAround(int row, int col, char character) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                int newRow = row + x;
                int newCol = col + y;
                if (isOnMap(newRow, newCol) && stringMap[newRow][newCol] == character) {
                    return true;
                }
            }
        }
        return false;
    }

    Coordinates coordinateOfCharacterAround(int row, int col) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                int newRow = row + x;
                int newCol = col + y;
                if (isOnMap(newRow, newCol) && stringMap[newRow][newCol] == '?') {
                    return new Coordinates(newRow, newCol);
                }
            }
        }
        return new Coordinates(row, col);
    }

    void exploreRegion(int row, int col, char character) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int newRow = row + x;
                int newCol = col + y;
                if (isOnMap(newRow, newCol) && stringMap[newRow][newCol] == character) {
                    stringMap[newRow][newCol] = '.';
                }
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int newRow = row + x;
                int newCol = col + y;
                if (isOnMap(newRow, newCol) && stringMap[newRow][newCol] == '@' && isCharacterAround(newRow, newCol, character)) {
                    exploreRegion(newRow, newCol, character);
                }
            }
        }
    }
}