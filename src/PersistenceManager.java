//This class loads a dictionary from a file and saves it back after modifications
public class PersistenceManager {

    PersistenceManager(String dictionaryFile) {}

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
