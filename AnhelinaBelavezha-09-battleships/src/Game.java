import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Game {
    private Status status;
    private Commands command;
    BattleMap myMap;
    BattleMap enemyMap;
    boolean isMyShoot;
    String move;
    Coordinates lastPositiveShot;
    Coordinates lastCoordinations;
    public Socket socket;
    public DataInputStream input;
    public DataOutputStream output;

    public Game(String path) {
        status = Status.TRWA;
        command = Commands.start;
        try {
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String mapInLine = reader.readLine();
            myMap = new BattleMap(10, 10, mapInLine);
            enemyMap = new BattleMap(10, 10);
            reader.readLine();
            reader.close();
        } catch (Exception e) {
            System.err.println("\u001B[31mError reading the file: " + e.getMessage() + "\u001B[0m");
        }
    }

    public void printMaps() {
        System.out.println("\u001B[32mMy map:\u001B[0m");
        myMap.printBoard();
        System.out.println("\u001B[31mEnemy map:\u001B[0m");
        enemyMap.printBoard();
    }

    Status getStatus() {
        return status;
    }

    Commands interpretCommandFromString(String str) {
        str = str.replace("\n", "");
        return switch (str) {
            case "start" -> Commands.start;
            case "pudło" -> Commands.pudło;
            case "trafiony" -> Commands.trafiony;
            case "trafiony zatopiony" -> Commands.trafiony_zatopiony;
            case "ostatni zatopiony" -> Commands.ostatni_zatopiony;
            default -> Commands.blad_komunikacji;
        };
    }

    String interpretCommandToString(Commands cmd) {
        String str = cmd.toString();
        str = str.replace('_', ' ');
        return str;
    }

    public void validateMessage(String message) throws IOException {
        if (message.contains(";")) {
            String[] messageParts = message.split(";");
            if (interpretCommandFromString(messageParts[0]) == Commands.blad_komunikacji) {
                throw new IOException("\u001B[31mInvalid command received.\u001B[0m");
            }
            Coordinates coordinates = new Coordinates(messageParts[1]);
            if (!MapGenerator.withinBoardLimits(enemyMap.getBoard(), coordinates.getX(), coordinates.getY())) {
                throw new IOException("\u001B[31mCoordinates out of bounds.\u001B[0m");
            }
        } else {
            throw new IOException("\u001B[31mMessage format incorrect.\u001B[0m");
        }
    }

    public String shootOptimalPlace() {
        if (isMyShoot && enemyMap.isCharacterAround(lastPositiveShot.getX(), lastPositiveShot.getY(), '?'))
            return enemyMap.coordinateOfCharacterAround(
                    lastPositiveShot.getX(),
                    lastPositiveShot.getY()
            ).toString();

        for (int row = 0; row < enemyMap.rows; row++) {
            for (int col = row % 2; col < enemyMap.cols; col += 2) {
                if (enemyMap.isCharacter(row, col, '?')) {
                    return new Coordinates(row, col).toString();
                }
            }
        }

        Coordinates optimalShot = new Coordinates(0, 0);

        for (int row = 0; row < enemyMap.rows; row++) {
            for (int col = 0; col < enemyMap.cols; col++) {
                if (enemyMap.isCharacter(row, col, '?')) {
                    optimalShot = new Coordinates(row, col);
                    row = enemyMap.rows;
                    col = enemyMap.cols;
                }
            }
        }

        return optimalShot.toString();
    }

    Commands evaluateShot(String strCoordinates) {
        Coordinates coordinates = new Coordinates(strCoordinates);
        boolean isIntactShipPart = myMap.isCharacter(coordinates.getX(), coordinates.getY(), '#');
        boolean isDamagedShipPart = myMap.isCharacter(coordinates.getX(), coordinates.getY(), '@');
        boolean noNearbyIntactShip = !myMap.isCharacterAround(coordinates.getX(), coordinates.getY(), '#');

        if (isIntactShipPart) {
            myMap.replaceCharacter(coordinates.getX(), coordinates.getY(), '@');
            if (!myMap.isCharacterOnMap('#'))
                return Commands.ostatni_zatopiony;

            if (noNearbyIntactShip)
                return Commands.trafiony_zatopiony;

            return Commands.trafiony;
        }
        if (isDamagedShipPart)
            return Commands.trafiony;

        return Commands.pudło;
    }


    public void refreshMap(Commands cmd, Coordinates coordinates, BattleMap map) {
        switch (cmd) {
            case pudło -> map.replaceCharacter(coordinates.getX(), coordinates.getY(), '~');
            case trafiony, trafiony_zatopiony, ostatni_zatopiony ->
                map.replaceCharacter(coordinates.getX(), coordinates.getY(), '@');
            default -> {}
        }
    }

    public void executeMoveProcess() {
        printMaps();
        if (!myMap.isCharacterOnMap('#')) status = Status.PRZEGRANA;
        System.out.println("Stan gry: " + status.toString());
        if (status == Status.TRWA) {
            System.out.println();
            System.out.print("Jakim bedzie twoj nastepny ruch? ");
            move = shootOptimalPlace();
            lastCoordinations = new Coordinates(move);
            System.out.println(move);
            move = interpretCommandToString(command) + ";" + move + "\n";
            System.out.println("Wysylam ruch: " + move);
            sendMoveProcess();
        } else if (status == Status.PRZEGRANA) {
            move = interpretCommandToString(Commands.ostatni_zatopiony) + "\n";
            System.out.println("Stan gry: PRZEGRANA. Wysylam ruch: " + move);
            sendMoveProcess();
        }
    }

    public void sendMoveProcess() {
        if (move == null) {
            System.err.println("\u001B[31mMove is null, cannot send move.\u001B[0m");
            return;
        }
        int retries = 3;
        while (retries > 0) {
            try {
                output.writeUTF(move);
                break;
            } catch (IOException e) {
                retries--;
                if (retries > 0) {
                    System.err.println("\u001B[33mRetrying to send move... (" + retries + " attempts left)\u001B[0m");
                } else {
                    System.err.println("\u001B[31mError receiving move: " + e.getMessage() + "\u001B[0m");
                }
            }
        }
    }

    public void receiveMoveProcess() {
        System.out.println("\u001B[3mCzekam na ruch...\u001B[0m");
        String incomingData = "";
        int retries = 3;
        while (retries > 0) {
            try {
                incomingData = input.readUTF();
                validateMessage(incomingData);
                break;
            } catch (IOException e) {
                retries--;
                if (retries > 0) {
                    System.err.println("\u001B[33mRetrying to receive move... (" + retries + " attempts left)\u001B[0m");
                    try {
                        socket.setSoTimeout(1000);
                        sendMoveProcess();
                    } catch (SocketException ignored) {
                    }
                } else {
                    System.err.println("\u001B[31mError receiving move: " + e.getMessage() + "\u001B[0m");
                }
            }
        }
        if (!incomingData.isEmpty()) {
            System.out.println("Otrzymana wiadomosc: " + incomingData);
            if (incomingData.contains(";")) {
                String[] msgParts = incomingData.split(";");
                Commands cmd = interpretCommandFromString(msgParts[0]);
                refreshMap(cmd, lastCoordinations, enemyMap);
                if (cmd == Commands.trafiony) {
                    isMyShoot = true;
                    lastPositiveShot = lastCoordinations;
                }
                if (cmd == Commands.trafiony_zatopiony) {
                    enemyMap.exploreRegion(lastCoordinations.getX(), lastCoordinations.getY(), '?');
                    isMyShoot = false;
                }
                command = evaluateShot(msgParts[1]);
                Coordinates cord = new Coordinates(msgParts[1]);
                refreshMap(command, cord, myMap);
            }
            if (interpretCommandFromString(incomingData) == Commands.ostatni_zatopiony) {
                refreshMap(Commands.ostatni_zatopiony, lastCoordinations, enemyMap);
                status = Status.WYGRANA;
            }
        }
    }

    public void updatePlayerDataStreams() {
        try {
            this.output = new DataOutputStream(this.socket.getOutputStream());
            this.input = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error updating player data streams: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            this.socket.close();
            this.input.close();
            this.output.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}