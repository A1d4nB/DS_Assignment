import java.io.IOException;

public class DictionaryClient {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port> <sleep-duration>");
        }
        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int sleepDuration = Integer.parseInt(args[2]);

    }
}
