package mycput.ac.za.openaiclient;

import okhttp3.*;
import com.google.gson.*;

public class OpenAIClient {
    private static final String API_KEY = "your-key";
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public static String askGPT(String prompt) throws Exception {
        OkHttpClient client = new OkHttpClient();

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject payload = new JsonObject();
        payload.addProperty("model", "gpt-4"); // or gpt-3.5-turbo
        payload.add("messages", messages);

        RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(ENDPOINT)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("Unexpected: " + response);

            String respBody = response.body().string();
            JsonObject json = JsonParser.parseString(respBody).getAsJsonObject();
            return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString().trim();
        }
    }
}
