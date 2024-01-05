import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        JenkinsPipeline jenkinsPipeline = new JenkinsPipeline("11ea59bb05f9b04e37f2b0e9c904117216", "admin");
        jenkinsPipeline.create("Hello", "file.xml");
        jenkinsPipeline.execute("Hello");
    }
}

class JenkinsPipeline {

    private String jenkinsToken;
    private String jenkinsUser;

    public JenkinsPipeline (String jenkinsToken, String jenkinsUser) {
        this.jenkinsToken = jenkinsToken;
        this.jenkinsUser = jenkinsUser;
    }

    public void create(String jobName, String patch) {
        String jenkinsUrl = "http://localhost:8080/";
        try {
            String createJobUrl = jenkinsUrl + "createItem?name=" + URLEncoder.encode(jobName, "UTF-8");

            Path xmlPath = Path.of(patch);
            String xmlContent = Files.readString(xmlPath);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(createJobUrl))
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((jenkinsUser + ":" + jenkinsToken).getBytes()))
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(xmlContent))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                System.out.println("Job created successfully!");
            } else {
                System.out.println("Failed to create job. Status code: " + statusCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void execute(String jobName) {
        String jenkinsUrl = "http://localhost:8080/";
        try {
            String jobUrl = jenkinsUrl + "job/" + jobName + "/build";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jobUrl))
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((jenkinsUser + ":" + jenkinsToken).getBytes()))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode == 201) {
                System.out.println("Job triggered successfully!");
            } else {
                System.out.println("Failed to trigger job. Status code: " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
