import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;


public class ClientHandler implements Runnable {
    private final Socket socket;
    private final DictionaryServerGUI dictionaryServerGUI;
    private final DictionaryManager dictionaryManager;

    public ClientHandler(Socket socket, DictionaryServerGUI gui, DictionaryManager dictionaryManager) {
        this.socket = socket;
        this.dictionaryServerGUI = gui;
        this.dictionaryManager = dictionaryManager;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(),true))
        {
            InetAddress clientAddress = socket.getInetAddress();
            String clientIpAddress = clientAddress.getHostAddress();
            String request;
            while ((request = br.readLine()) != null) {
                dictionaryServerGUI.logMessage("Received request: " + request + " from " + clientIpAddress);
                String result = requestHandle(request);
                dictionaryServerGUI.logMessage(result);
                pw.println(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String requestHandle(String request) {
        String[] split = request.split("\\*");
        String action = split[0].toUpperCase();

        switch (action) {
            case "QUERY":
                String word = split[1].toLowerCase();
                int duration = Integer.parseInt(split[2]);
                List<String> meanings = dictionaryManager.query(word, duration);


                if (meanings == null || meanings.isEmpty()) {
                    return "Unsuccessful: Word not found";
                } else {
                    return "Success: Meanings: " + meanings.toString();
                }

            case "ADD":
                String addWord = split[1].toLowerCase();
                List<String> addMeanings = Arrays.asList(split[2].toLowerCase().split(","));
                duration = Integer.parseInt(split[3]);
                return dictionaryManager.addWord(addWord, addMeanings, duration) ? "Success: Added word" :
                                                                                    "Unsuccessful: Duplicate word";

            case "REMOVE":
                String removeWord = split[1].toLowerCase();
                duration = Integer.parseInt(split[2]);
                return dictionaryManager.removeWord(removeWord, duration) ? "Success: Word removed" :
                                                                                    "Unsuccessful: Word does not exist";

            case "ADDMEANING":
                String existingWord = split[1].toLowerCase();
                String existingMeaning = split[2].toLowerCase();
                duration = Integer.parseInt(split[3]);
                return dictionaryManager.addMeaning(existingWord, existingMeaning, duration) ? "Success: Meaning successfully added" :
                                                                                        "Unsuccessful: Word does not exist";
            case "UPDATEMEANING":
                String wordUpdating = split[1];
                String meaningUpdating = split[2];
                String newMeaning = split[3];
                duration = Integer.parseInt(split[4]);
                return dictionaryManager.changeMeaning(wordUpdating, meaningUpdating, newMeaning, duration) ? "Success: Meaning successfully updated" :
                                                                                                        "Unsuccessful: Word or meaning does not exist";
        }

        return "Unknown request";
    }
}
