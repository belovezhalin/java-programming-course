import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.io.IOException;

public class Main {
    static String path = "map.txt";
    static Scanner scanner = new Scanner(System.in);

    public static void createFile(String str) throws IOException {
        Path filePath = Path.of(path);
        Files.deleteIfExists(filePath);
        Files.writeString(filePath, str);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("\u001B[32mWelcome to Battleships! Are you a Player or a Server?\u001B[0m");
        String mode = scanner.nextLine();

        MapGenerator generator = new MapGenerator();
        String map = generator.generateMap();
        createFile(map);

        switch (mode) {
            case "Server":
                Server server = new Server(path);
                System.out.println("\u001B[32mServer started!\u001B[0m");
                server.play();
                break;
            case "Player":
                System.out.println("\u001B[32mWhat is the port number?\u001B[0m");
                int port = Integer.parseInt(scanner.nextLine());
                System.out.println("\u001B[32mWhat is the server IP?\u001B[0m");
                String serverIP = scanner.nextLine();
                Client client = new Client(serverIP, port, path);
                System.out.println("\u001B[32mClient started!\u001B[0m");
                client.play();
                break;
            default:
                System.out.println("\u001B[31mInvalid mode selected.\u001B[0m");
                break;
        }
        scanner.close();
    }
}