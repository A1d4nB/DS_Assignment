import java.net.Socket;

//Adding return boolean to each method to communicate status if successful or not
public class DictionaryManager {

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
