public class Coordinates {
    private final Integer x;
    private final Integer y;

    public Coordinates(String stringCoordinates) {
        x = stringCoordinates.charAt(0) - 'A';
        y = Integer.parseInt(stringCoordinates.substring(1).trim());
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.valueOf((char) (x + 'A')) + y;
    }
}