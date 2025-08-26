import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPThreadPoolServer {
    private final ExecutorService threadPool;
    private final DictionaryManager dictionaryManager;

    public TCPThreadPoolServer(int poolSize, String dictionaryFile) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.dictionaryManager = new DictionaryManager(dictionaryFile);
    }

    //This won't be here as we will have ClientHandler class taking care of this
    public void handleClient(Socket clientSocket) {
        try (PrintWriter toSocket = new PrintWriter(clientSocket.getOutputStream(), true)) {
            toSocket.println("Hello from server " + clientSocket.getInetAddress());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //Read this from args.
        if (args.length < 2) {
            System.err.println("Usage: java -jar DictionaryServer.jar <port> <dictionary-file>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        String dictionaryFile = args[1];

        int poolSize = 100; // Adjust the pool size as needed
        TCPThreadPoolServer server = new TCPThreadPoolServer(poolSize, dictionaryFile);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(70000);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Use the thread pool to handle the client
                //This will instead create a ClientHandler class instead of what is below.
                //The ClientHandler will be passed a clientSocket and a dictionaryManager
                server.threadPool.execute(() -> server.handleClient(clientSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Shutdown the thread pool when the server exits
            server.threadPool.shutdown();
        }
    }
}
