package com.lbadvisors.pffc.controllers.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbadvisors.pffc.controllers.inventory.Inventory;
import com.lbadvisors.pffc.controllers.inventory.InventoryRepository;
import com.lbadvisors.pffc.controllers.orders.Order;
import com.lbadvisors.pffc.controllers.orders.OrderRepository;
import com.lbadvisors.pffc.controllers.profiles.Profile;
import com.lbadvisors.pffc.controllers.profiles.ProfileRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

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

    private String oneDriveTokenUrl;
    private String oneDriveClientId;
    private String oneDriveClientSecret;
    private String oneDriveGrapApiBaseUrl;
    private String oneDriveFolderName;
    private String SCOPE = "https://graph.microsoft.com/.default";

    public OneDriveService(@Value("${onedrive.tenant.id}") String oneDriveTenantId, @Value("${onedrive.client.secret}") String oneDriveClientSecret,
            @Value("${onedrive.client.id}") String oneDriveClientId, @Value("${onedrive.user.email}") String oneDriveUserEmail,
            @Value("${onedrive.folder.name}") String oneDriveFolderName) {
        oneDriveTokenUrl = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", oneDriveTenantId);
        oneDriveGrapApiBaseUrl = String.format("https://graph.microsoft.com/v1.0/users/%s/drive/root:/", oneDriveUserEmail);
        this.oneDriveClientId = oneDriveClientId;
        this.oneDriveClientSecret = oneDriveClientSecret;
        this.oneDriveFolderName = oneDriveFolderName;
    }

    private String getAccessToken() throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(oneDriveTokenUrl);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("client_id", oneDriveClientId));
        urlParameters.add(new BasicNameValuePair("client_secret", oneDriveClientSecret));
        urlParameters.add(new BasicNameValuePair("scope", SCOPE));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        CloseableHttpResponse response = client.execute(post);

        // Check response status
        if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
            throw new IOException("Cannot retrieve authentication token");
        }

        String jsonResponse = EntityUtils.toString(response.getEntity());
        JsonNode tokenNode = new ObjectMapper().readTree(jsonResponse);
        return tokenNode.get("access_token").asText();
    }

    // Handle and parse the error response from Microsoft Graph API
    private void handleGraphApiError(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        String errorCode;
        String errorMessage;

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode errorNode = rootNode.path("error");

            errorCode = errorNode.path("code").asText();
            errorMessage = errorNode.path("message").asText();

        } catch (Exception e) {
            throw new IOException("Unexpected Graph API response: " + responseBody, e);
        }
        throw new IOException("Graph API Error - Code: " + errorCode + " - Message: " + errorMessage);
    }

    private InputStream getFileContent(String filePath) throws IOException {

        String encodedFilePath = URLEncoder.encode(filePath, "UTF-8").replaceAll("\\+", "%20");

        String accessToken = getAccessToken();

        String fileDownloadUrl = oneDriveGrapApiBaseUrl + encodedFilePath + ":/content";
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(fileDownloadUrl);
        request.addHeader("Authorization", "Bearer " + accessToken);

        CloseableHttpResponse response = client.execute(request);

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
    private void uploadFile(String filePath, String fileContent) throws IOException {

        String encodedFilePath = URLEncoder.encode(filePath, "UTF-8").replaceAll("\\+", "%20");

        String accessToken = getAccessToken(); // Get the access token

        // The URL to upload the file content to OneDrive
        String uploadUrl = oneDriveGrapApiBaseUrl + encodedFilePath + ":/content";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Create a PUT request to upload the file
            HttpPut httpPut = new HttpPut(uploadUrl);
            httpPut.setHeader("Authorization", "Bearer " + accessToken);
            httpPut.setHeader("Content-Type", "text/plain");

            // Set the file content in the body of the request
            StringEntity entity = new StringEntity(fileContent);
            httpPut.setEntity(entity);

            // Execute the request
            CloseableHttpResponse response = client.execute(httpPut);

            // Check response status
            if (response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 201) {
                String responseBody = EntityUtils.toString(response.getEntity());
                handleGraphApiError(responseBody);
            }
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    protected void updateOrderFile() {

        final String filename = oneDriveFolderName + "/" + "Orders.csv";

        LocalDateTime twoMonthsAgo = LocalDateTime.now().minus(2, ChronoUnit.MONTHS);
        List<Order> orders = orderRepository.findByCreatedAtAfter(twoMonthsAgo);

        // Define header
        String[] header = { "id", "customer_name", "sales_rep_name", "profile_description", "unit_type", "pack_size", "price", "quantity", "total_price", "delivery_date",
                "customer_id", "customer_email", "sales_rep_phone", "order_id", "customer_po", "profile_id", "profile_did", "ship_to_id", "ship_to_name", "created_by",
                "created_at", "last_updated_by", "last_updated_at" };

        StringWriter writer = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(header);

            // Write user data to CSV
            for (Order order : orders) {
                String[] data = { String.valueOf(order.getId()), order.getCustomerName(), order.getSalesRepName(), order.getProfileDescription(), order.getUnitType(),
                        String.valueOf(order.getPackSize()), String.valueOf(order.getPrice()), String.valueOf(order.getQuantity()), String.valueOf(order.getTotalPrice()),
                        String.valueOf(order.getDeliveryDate()), String.valueOf(order.getCustomerId()), order.getCustomerEmail(), order.getSalesRepPhone(),
                        String.valueOf(order.getOrderId()), order.getCustomerPo(), String.valueOf(order.getProfileDid()), String.valueOf(order.getShipToId()),
                        order.getShipToName(), order.getShipToName(), order.getCreatedBy(), String.valueOf(order.getCreatedAt()), order.getLastUpdatedBy(),
                        String.valueOf(order.getLastUpdatedAt()) };
                csvWriter.writeNext(data);

            }

            uploadFile(filename, writer.toString());

        } catch (IOException ex) {
            logMessage("Error in '" + filename + "': " + ex.getMessage());
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    protected void refreshProfileTableFromCsv() {

        final String filename = oneDriveFolderName + "/" + "Profiles.csv";

        try {

            // Fetch the CSV file content
            InputStream inputStream = getFileContent(filename);

            // Read the CSV file
            try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                csvReader.readNext(); // Reads and discards the first line (header)

                List<Profile> profiles = new ArrayList<>();

                String[] values;
                while ((values = csvReader.readNext()) != null) {
                    try {
                        Profile profile = new Profile();
                        if (values[0].length() > 0)
                            profile.setProfileDid(Integer.parseInt(values[0]));
                        profile.setSalesRepName(values[1]);
                        profile.setSalesRepPhone(values[2]);
                        profile.setSalesRepEmail(values[3]);
                        if (values[4].length() > 0)
                            profile.setProfileId(Integer.parseInt(values[4]));
                        if (values[5].length() > 0)
                            profile.setCustomerId(Integer.parseInt(values[5]));
                        profile.setCustomerName(values[6]);
                        if (values[7].length() > 0)
                            profile.setCompItemId(Integer.parseInt(values[7]));
                        profile.setProfileDescription(values[8]);
                        profile.setProfileInstruction(values[9]);
                        profile.setUnitType(values[10]);
                        if (values[11].length() > 0)
                            profile.setPackSize(new BigDecimal(values[11]));
                        if (values[12].length() > 0)
                            profile.setPrice(new BigDecimal(values[12]));
                        profile.setCustomerContactName(values[13]);
                        profile.setCustomerCell(values[14]);
                        profile.setCustomerEmail(values[15]);
                        profile.setCompanyName(values[16]);
                        if (values[17].length() > 0)
                            profile.setCompanyId(Integer.parseInt(values[17]));

                        profiles.add(profile);

                    } catch (NumberFormatException ex) {
                        logMessage("Error in '" + filename + "': " + ex.getMessage());
                    }
                }
                profileRepository.deleteAllProfilesInBulk();
                bulkInsert(profiles, "profiles");
            }
        } catch (IOException | CsvValidationException ex) {

            logMessage("Error in '" + filename + "': " + ex.getMessage());
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    protected void refreshInventoryTableFromCsv() {

        final String filename = oneDriveFolderName + "/" + "Inventory.csv";

        try {

            // Fetch the CSV file content
            InputStream inputStream = getFileContent(filename);

            // Read the CSV file
            try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                csvReader.readNext(); // Reads and discards the first line (header)

                List<Inventory> inventories = new ArrayList<>();

                String[] values;
                while ((values = csvReader.readNext()) != null) {
                    try {
                        Inventory inventory = new Inventory();

                        inventory.setCompDescription(values[1]);
                        inventory.setUnitType(values[2]);
                        inventory.setPackSize(values[3]);
                        if (values[4].length() > 0)
                            inventory.setActivePrice(new BigDecimal(values[4]));
                        inventory.setWoh(values[5]);

                        inventories.add(inventory);

                    } catch (NumberFormatException ex) {
                        logMessage("Error in '" + filename + "': " + ex.getMessage());
                    }
                }
                inventoryRepository.deleteAllInventoriesInBulk();
                bulkInsert(inventories, "inventories");
            }

        } catch (IOException | CsvValidationException ex) {

            logMessage("Error in '" + filename + "': " + ex.getMessage());
        }
    }

    @Transactional
    private <T> void bulkInsert(List<T> items, String name) {

        final int batchSize = 100;
        for (int i = 0; i < items.size(); i++) {
            entityManager.persist(items.get(i));

            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        // Final flush to ensure the last batch is inserted
        entityManager.flush();
        entityManager.clear();
    }

    private synchronized void logMessage(String message) {

        logger.error(message);

        final String filename = oneDriveFolderName + "/" + "Errors.log";

        try {
            InputStream inputStream = getFileContent(filename);

            String logMessages = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

            logMessages = LocalDateTime.now().format(formatter) + ": " + message + System.lineSeparator() + logMessages;

            uploadFile(filename, logMessages);

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}