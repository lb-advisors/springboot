package com.lbadvisors.pffc.controllers.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbadvisors.pffc.controllers.inventory.InventoryRepository;
import com.lbadvisors.pffc.controllers.orders.OrderRepository;
import com.lbadvisors.pffc.controllers.profiles.ProfileRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class OneDriveService {

    private static final Logger logger = LoggerFactory.getLogger(OneDriveService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    private final CloseableHttpClient httpClient;

    private String oneDriveTokenUrl;
    private String oneDriveClientId;
    private String oneDriveClientSecret;
    private String oneDriveGrapApiBaseUrl;
    private String oneDriveFolderName;
    private String SCOPE = "https://graph.microsoft.com/.default";

    public OneDriveService(CloseableHttpClient httpClient, @Value("${onedrive.tenant.id}") String oneDriveTenantId, @Value("${onedrive.client.secret}") String oneDriveClientSecret,
            @Value("${onedrive.client.id}") String oneDriveClientId, @Value("${onedrive.user.email}") String oneDriveUserEmail,
            @Value("${onedrive.folder.name}") String oneDriveFolderName) {
        this.httpClient = httpClient;
        this.oneDriveTokenUrl = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", oneDriveTenantId);
        this.oneDriveGrapApiBaseUrl = String.format("https://graph.microsoft.com/v1.0/users/%s/drive/root:/", oneDriveUserEmail);
        this.oneDriveClientId = oneDriveClientId;
        this.oneDriveClientSecret = oneDriveClientSecret;
        this.oneDriveFolderName = oneDriveFolderName;
    }

    @Retryable(include = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 3000, maxDelay = 20000, random = true))
    private String getAccessToken() throws IOException {
        HttpPost post = new HttpPost(oneDriveTokenUrl);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("client_id", oneDriveClientId));
        urlParameters.add(new BasicNameValuePair("client_secret", oneDriveClientSecret));
        urlParameters.add(new BasicNameValuePair("scope", SCOPE));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpResponse response = httpClient.execute(post)) {

            // Check response status
            if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
                throw new IOException("Cannot retrieve authentication token");
            }

            String jsonResponse = EntityUtils.toString(response.getEntity());
            JsonNode tokenNode = new ObjectMapper().readTree(jsonResponse);
            return tokenNode.get("access_token").asText();
        }
    }

    // Handle and parse the error response from Microsoft Graph API
    private void handleGraphApiError(String responseBody) throws IOException {

        String errorCode;
        String errorMessage;

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode errorNode = rootNode.path("error");

            errorCode = errorNode.path("code").asText();
            errorMessage = errorNode.path("message").asText();

        } catch (Exception e) {
            throw new IOException("Unexpected Graph API response: " + responseBody, e);
        }
        throw new IOException("Graph API Error - Code: " + errorCode + " - Message: " + errorMessage);
    }

    @Retryable(include = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 3000, maxDelay = 20000, random = true))
    public InputStream getFileContent(String filePath) throws IOException, ClientProtocolException {

        logger.info("getFileContent");

        String encodedFilePath = URLEncoder.encode(filePath, "UTF-8").replaceAll("\\+", "%20");

        String accessToken = getAccessToken();

        String fileDownloadUrl = oneDriveGrapApiBaseUrl + encodedFilePath + ":/content";

        HttpGet request = new HttpGet(fileDownloadUrl);
        request.addHeader("Authorization", "Bearer " + accessToken);

        CloseableHttpResponse response = httpClient.execute(request);

        // Check response status
        if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
            // If an error occurs, parse the response to get error code and message
            String responseBody = EntityUtils.toString(response.getEntity());
            handleGraphApiError(responseBody);
        }
        return response.getEntity().getContent();
    }

    /**
     * Writes content to a OneDrive file. If the file exists, it will be
     * overwritten.
     * 
     * @param filePath    The OneDrive file path
     * @param fileContent The content to be written to the file
     * @throws IOException If an error occurs during file upload
     */
    @Retryable(include = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 3000, maxDelay = 20000, random = true))
    public void uploadFile(String filePath, String fileContent) throws IOException {

        String encodedFilePath = URLEncoder.encode(filePath, "UTF-8").replaceAll("\\+", "%20");

        String accessToken = getAccessToken(); // Get the access token

        // The URL to upload the file content to OneDrive
        String uploadUrl = oneDriveGrapApiBaseUrl + encodedFilePath + ":/content";

        // Create a PUT request to upload the file
        HttpPut httpPut = new HttpPut(uploadUrl);
        httpPut.setHeader("Authorization", "Bearer " + accessToken);
        httpPut.setHeader("Content-Type", "text/plain");

        // Set the file content in the body of the request
        StringEntity entity = new StringEntity(fileContent);
        httpPut.setEntity(entity);

        // Execute the request
        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {

            // Check response status
            if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
                String responseBody = EntityUtils.toString(response.getEntity());
                handleGraphApiError(responseBody);
            }
        }

    }

    public synchronized void logErrorMessage(String message) {

        logger.error("Entering logErrorMessage: " + message);

        final String filename = oneDriveFolderName + "/" + "Errors.log";

        try (InputStream inputStream = getFileContent(filename)) {

            String logErrorMessages = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

            logErrorMessages = LocalDateTime.now().format(formatter) + ": " + message + System.lineSeparator() + logErrorMessages;

            uploadFile(filename, logErrorMessages);

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
}