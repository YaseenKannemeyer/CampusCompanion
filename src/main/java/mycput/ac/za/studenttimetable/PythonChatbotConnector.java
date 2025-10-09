package mycput.ac.za.studenttimetable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

    import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PythonChatbotConnector {

    // URL where your Flask server is running
    private static final String SERVER_URL = "http://127.0.0.1:5000/ask";


public static String getBotReply(String userMessage) {
    try {
        URL url = new URL(SERVER_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = "{\"message\": \"" + userMessage.replace("\"", "\\\"") + "\"}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (response.toString().isEmpty()) return "...";

        // Parse JSON to get the actual reply
        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        return jsonObject.get("response").getAsString();

    } catch (Exception e) {
        e.printStackTrace();
        return "⚠️ Error: Could not communicate with chatbot.";
    }
}

}
