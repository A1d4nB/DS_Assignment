import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPThreadPoolServer {
    private final ExecutorService threadPool;
    private final DictionaryManager dictionaryManager;
    private final int serverPort;
    private ServerSocket serverSocket;
    private DictionaryServerGUI serverGUI;
    private boolean serverRunning = false;

    public TCPThreadPoolServer(int poolSize, int port, String dictionaryFile) throws FileNotFoundException {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.dictionaryManager = new DictionaryManager(dictionaryFile);
        this.serverPort = port;
    }

    public void start(DictionaryServerGUI gui) {
        try {
            startSocketServer();
            this.serverGUI = gui;
            gui.setConnectionStatus(true);
            gui.logMessage("Server is listening on port " + serverPort);

            while (true) {
                while (serverRunning) {
                    Socket clientSocket = serverSocket.accept();
                    this.threadPool.execute(new ClientHandler(clientSocket, serverGUI, dictionaryManager));
                    serverGUI.setConnCount(serverGUI.getConnCount() + 1);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            this.threadPool.shutdown();
            gui.setConnectionStatus(false);
        }
    }

    public void startSocketServer() {
        try {
            serverSocket = new ServerSocket(serverPort);
            serverRunning = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stopSocketServer() {
        try {
            serverSocket.close();
            serverRunning = false;
        } catch (IOException ex) {}
    }

    public int getServerPort() {
        return serverPort;
    }
}
