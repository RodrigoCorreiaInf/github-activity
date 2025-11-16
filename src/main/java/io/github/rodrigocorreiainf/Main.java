package io.github.rodrigocorreiainf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String username = "kamranahmedse";

        Main main = new Main();

        main.fetchGithubActivity(username);


    }

    private void fetchGithubActivity(String username) throws IOException, InterruptedException {
        String GITHUB_URI = "https://api.github.com/users/" + username + "/events";
        ObjectMapper mapper = new ObjectMapper();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GITHUB_URI)).build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 404) {
            System.out.println("Username not found.");
        }

        if (httpResponse.statusCode() == 200) {
            JsonNode node = mapper.readTree(httpResponse.body());

            if (node.isArray()) {
                ArrayNode events = (ArrayNode) node;
                display(events);
            }


        }
    }

    private void display(ArrayNode events) {

        for (JsonNode event : events) {
            String type = event.get("type").toString();
            String action;
            switch (type) {
                case "\"PushEvent\"" -> System.out.println(event);
                case "CreateEvent" -> System.out.println("create");
            }


        }

    }

}