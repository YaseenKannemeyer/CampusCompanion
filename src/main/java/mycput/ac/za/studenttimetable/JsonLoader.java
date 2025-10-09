package mycput.ac.za.studenttimetable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonLoader {

    private static final String JSON_FILE = "knowledge.json";

    public static List<QA> loadQA() {
        List<QA> qaList = new ArrayList<>();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(JSON_FILE)) {
            Type listType = new TypeToken<List<QA>>() {}.getType();
            qaList = gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Failed to load JSON file: " + JSON_FILE);
            e.printStackTrace();
        }
        return qaList;
    }
}
