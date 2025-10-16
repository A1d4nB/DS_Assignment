import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;


public class DictionaryManager {
    private Map<String, List<String>> dictionary;
    private final String dictionaryFile;
    private final Gson gson = new Gson();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public DictionaryManager(String df) throws FileNotFoundException {
        this.dictionaryFile = df;
        this.dictionary = loadDictionary(dictionaryFile);
    }

    public boolean addWord(String word, List<String> meanings, int duration) {
        lock.writeLock().lock();
        try {
            if (word == null || word.isBlank() || meanings == null || meanings.isEmpty()) {
                return false;
            }
            if (dictionary.containsKey(word)) {
                return false;
            }
            dictionary.put(word, new ArrayList<>(meanings));
            saveDictionary(dictionary);
            try {
                sleep(duration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }   finally {
            lock.writeLock().unlock();
        }
    }
    public boolean removeWord(String word, int duration) {
        lock.writeLock().lock();
        try {
        if(!dictionary.containsKey(word)) {
          return false;
        }

        dictionary.remove(word);
        saveDictionary(dictionary);
        try {
            sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    public boolean addMeaning(String word,  String meaning, int duration) {
        lock.writeLock().lock();
        try {
            List<String> meanings = dictionary.get(word);
            if (meanings == null || meaning.isEmpty()) {
                return false;
            }

            if (meanings.contains(meaning)) {
                return false;
            }

            meanings.add(meaning);
            saveDictionary(dictionary);
            try {
                sleep(duration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }  finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> query(String word, int duration) {
        lock.readLock().lock();
        try {
            try {
                sleep(duration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return dictionary.get(word);
        } finally {
            lock.readLock().unlock();
        }
    }
    public boolean changeMeaning(String word, String meaning, String newMeaning, int duration) {
        lock.writeLock().lock();
        try {
            List<String> meanings = dictionary.get(word);

            if (meanings == null || !dictionary.containsKey(word) || !meanings.contains(meaning)) {
                return false;
            }
            if (meanings.contains(newMeaning)) {
                return false;
            }
            int idx = meanings.indexOf(meaning);
            meanings.set(idx, newMeaning);
            saveDictionary(dictionary);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Map<String, List<String>> loadDictionary(String dictionaryFile) {
        try (FileReader fr = new FileReader(dictionaryFile)) {
            Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
            Map<String, List<String>> loaded = gson.fromJson(fr, type);
            return (loaded != null) ? loaded : new HashMap<>();
        } catch (IOException e) {
            System.out.println("File not found, creating an empty dictionary.");
            return new HashMap<>();
        }
    }

    private void saveDictionary(Map<String, List<String>> dictionary) {
        try (FileWriter fw = new FileWriter(dictionaryFile)) {
            gson.toJson(dictionary, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
