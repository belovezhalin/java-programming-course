import java.net.Socket;
import java.io.IOException;

public class Client {
    Game game;

    public Client(String ip, int port, String path) {
        this.game = new Game(path);
        try {
            game.socket = new Socket(ip, port);
            game.updatePlayerDataStreams();
        } catch (IOException e) {
            System.err.println("\u001B[31mConnection error: " + e.getMessage() + "\u001B[0m");
        }
    }

    public void play() {
        while (game.getStatus() == Status.TRWA) {
            game.executeMoveProcess();
            game.receiveMoveProcess();
        }
        System.out.println(game.getStatus() == Status.PRZEGRANA ? "\u001B[31mPRZEGRANA\u001B[0m" : "\u001B[32mWYGRANA\u001B[0m");
        game.printMaps();
        game.closeConnection();
    }
}