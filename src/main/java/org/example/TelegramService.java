package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramService {

    private static final String BOT_TOKEN = "TELEGRAM_BOT_TOKEN";
    private static final String CHAT_ID = "TELEGRAM_CHAT_ID";

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void sendMessage(String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage"
                    + "?chat_id=" + CHAT_ID
                    + "&text=" + encodedText;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
