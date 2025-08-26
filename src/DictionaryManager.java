import java.net.Socket;
import java.util.List;
import java.util.Map;

//Have maybe switch statements here on a string request input that will separate it into two parts,
//And send the request to the PersistenceManager who will directly execute the command, modify the dictionary
//And return the modified dictionary

public class DictionaryManager {
    private final PersistenceManager persistenceManager;
    private final Map<String, List<String>> dictionary;

    public DictionaryManager(String dictionaryFile) {
        this.persistenceManager = new PersistenceManager(dictionaryFile);
    }




    protected String processRequest(String request) {
        //Split string here somehow to extract the request itself and the following words
        //Then use Case switch statements to pass it to persistenceManager
        return request;
    }
    synchronized public boolean addWord(String word) {

        return false;
    }
    synchronized public boolean removeWord(String word) {

        return false;
    }
    synchronized public boolean addMeaning(String word,  String meaning) {

        return false;
    }
    //not entirely sure what query does
    synchronized public boolean query(String word) {
        return false;
    }
    synchronized public boolean changeMeaning(String word, String meaning) {
        return false;
    }
}
