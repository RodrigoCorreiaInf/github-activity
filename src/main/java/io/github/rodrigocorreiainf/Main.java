package io.github.rodrigocorreiainf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            System.out.println("Usage: java Main get <username>");
            return;
        }

        String username = args[0];

        Main main = new Main();
        main.fetchGithubActivity(username);
    }

    private void fetchGithubActivity(String username) throws IOException, InterruptedException {
        String GITHUB_URI = String.format("https://api.github.com/users/%s/events", username);
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
            String type = event.get("type").asText();
            String repoName = event.get("repo").get("name").asText();
            switch (type) {
                case "PushEvent" -> {
                    JsonNode payload = event.get("payload");
                    int commits = payload.has("commits") ? payload.get("commits").size() : 1;
                    System.out.println("- Pushed " + commits + " commits to " + repoName);
                }
                case "CreateEvent" -> {
                    JsonNode payload = event.get("payload");
                    String ref_type = payload.get("ref_type").asText();
                    String ref = payload.get("ref").asText();
                    switch (ref_type) {
                        case "branch" -> System.out.println("- Created a new branch '" + ref + "' in " + repoName);
                        case "repository" -> System.out.println();
                        default -> System.out.printf("- Created a new '" + ref_type + "' in " + repoName);
                    }
                }
                case "PullRequestEvent" -> {
                    JsonNode payload = event.get("payload");
                    String action = capitalize(payload.get("action").asText());
                    System.out.println("- " + action + " pull request in " + repoName);
                }
                case "DeleteEvent" -> {
                    JsonNode payload = event.get("payload");
                    String ref_type = payload.get("ref_type").asText();
                    System.out.println("- Deleted " + ref_type + " in " + repoName);
                }
                case "ReleaseEvent" -> {
                    System.out.println("- Published release in " + repoName);
                }
                case "WatchEvent" -> System.out.println("- Starred " + repoName);
                case "ForkEvent" -> System.out.println("- Forked " + repoName);
                default -> System.out.println("- Event " + type + " on " + repoName);
            }

        }

    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

}