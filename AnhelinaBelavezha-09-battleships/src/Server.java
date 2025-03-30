
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.IOException;

public class Server {
    private ServerSocket server;
    Game game;

    public Server(String file) {
        try {
            game = new Game(file);
            InetAddress ip = InetAddress.getLocalHost();
            server = new ServerSocket(0, 1, ip);
            System.out.print("\u001B[32mServer running at: " + server.getLocalSocketAddress() + "\u001B[0m");
            game.socket = server.accept();
            game.updatePlayerDataStreams();
        } catch (IOException e) {
        System.err.println("\u001B[31mServer initialization failed: " + e.getMessage() + "\u001B[0m");
        }
    }

    public void play() {
        while (game.getStatus() == Status.TRWA) {
            game.receiveMoveProcess();
            game.executeMoveProcess();
        }
        System.out.println(game.getStatus() == Status.PRZEGRANA ? "\u001B[31mPRZEGRANA\u001B[0m" : "\u001B[32mWYGRANA\u001B[0m");
        game.printMaps();
        try {
            server.close();
        } catch (IOException e) {
            System.err.println("\u001B[31mError closing server: " + e.getMessage() + "\u001B[0m");
        }
        game.closeConnection();
    }
}