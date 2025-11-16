package io.github.rodrigocorreiainf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A simple CLI application that fetches and displays a GitHub user's
 * public activity events using the GitHub REST API.
 *
 * <p>This application takes a GitHub username as a command-line argument and
 * retrieves their recent events, printing them in a human-readable format.</p>
 */
public class Main {

    /**
     * Entry point of the application.
     *
     * @param args command-line arguments. Expected: {@code <username>}
     *
     * @throws IOException          if an I/O error occurs while making the HTTP request.
     * @throws InterruptedException if the HTTP request is interrupted.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            System.out.println("Usage: java Main get <username>");
            return;
        }

        String username = args[0];

        Main main = new Main();
        main.fetchGithubActivity(username);
    }

    /**
     * Fetches recent GitHub activity events for the specified user and displays them.
     *
     * <p>Makes a GET request to {@code https://api.github.com/users/<username>/events}
     * and parses the returned JSON response.</p>
     *
     * @param username the GitHub username to retrieve activity for.
     *
     * @throws IOException          if an error occurs during HTTP communication or JSON parsing.
     * @throws InterruptedException if the HTTP request is interrupted.
     */
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

    /**
     * Displays a list of GitHub activity events in a user-friendly format.
     *
     * <p>Each event type (push, pull request, star, fork, etc.) is handled separately
     * to produce readable descriptions.</p>
     *
     * @param events the array of GitHub event objects returned by the API.
     */
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

    /**
     * Capitalizes the first letter of a string.
     *
     * @param text the input string.
     * @return the string with its first character capitalized,
     * or the original string if empty or null.
     */
    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

}