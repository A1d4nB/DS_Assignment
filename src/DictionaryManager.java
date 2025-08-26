import java.net.Socket;

//Have maybe switch statements here on a string request input that will separate it into two parts,
//And send the request to the PersistenceManager who will directly execute the command, modify the dictionary
//And return the modified dictionary

public class DictionaryManager {
    private final PersistenceManager persistenceManager;

    public DictionaryManager(String dictionaryFile) {
        this.persistenceManager = new PersistenceManager(dictionaryFile);
    }

    protected String processRequest(String request) {
        //Split string here somehow to extract the request itself and the following words
        //Then use Case switch statements to pass it to persistenceManager
    }
}
