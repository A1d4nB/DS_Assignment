import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

//This class loads a dictionary a file and saves it back after modifications
public class PersistenceManager {
    private final Gson gson = new Gson();
    private final String dictionaryFile;

    public PersistenceManager(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }

    //DictinonaryServer will call PersistenceManager.fromJson() at startup
    public Map<String, List<String>> loadDictionary(String dictionaryFile) throws FileNotFoundException {
        FileReader fr = new FileReader(dictionaryFile);
        Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
        return gson.fromJson(fr, type);
    }

    //DictionaryManager will call PersistenceManager.toJson() after any modification is made
    public void saveDictionary(Map<String, List<String>> dictionary) throws IOException {
        FileWriter fw = new FileWriter(dictionaryFile);
        gson.toJson(dictionary, fw);
    }
}

