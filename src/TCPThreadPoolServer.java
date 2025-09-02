import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPThreadPoolServer {
    private final ExecutorService threadPool;
    private final DictionaryManager dictionaryManager;
    private final int serverPort;

    public TCPThreadPoolServer(int poolSize, int port, String dictionaryFile) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.dictionaryManager = new DictionaryManager(dictionaryFile);
        this.serverPort = port;
    }


    public void start() {

        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            //serverSocket.setSoTimeout(70000);
            System.out.println("Server is listening on port " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Use the thread pool to handle the client
                //This will instead create a ClientHandler class instead of what is below.
                //The ClientHandler will be passed a clientSocket and a dictionaryManager
                this.threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Shutdown the thread pool when the server exits
            this.threadPool.shutdown();
        }
    }

    public int getServerPort() {
        return serverPort;
    }
}
