package com.lbadvisors.pffc.controllers.jobs;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.lbadvisors.pffc.controllers.inventory.Inventory;
import com.lbadvisors.pffc.controllers.inventory.InventoryRepository;
import com.lbadvisors.pffc.controllers.orders.Order;
import com.lbadvisors.pffc.controllers.orders.OrderRepository;
import com.lbadvisors.pffc.controllers.profiles.Profile;
import com.lbadvisors.pffc.controllers.profiles.ProfileRepository;
import com.lbadvisors.pffc.util.AwsService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class GoogleDriveService {

    private static final Logger logger = LoggerFactory.getLogger(AwsService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Value("${google.drive.application.name}")
    private String googleDriveApplicationName;

    @Value("${google.drive.folder.id}")
    private String googleDriveFolderId;

    private Drive driveService;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public GoogleDriveService(@Value("${google.drive.credentials}") String googleDriveCredentials) {
        try {
            initializeDriveService(googleDriveCredentials);
        } catch (IOException | GeneralSecurityException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * Initializes Google Drive client service using a service account.
     * 
     * @return Drive client service.
     * @throws IOException              if the service account file cannot be read.
     * @throws GeneralSecurityException if there's a security issue.
     */
    private void initializeDriveService(String googleDriveCredentials) throws IOException, GeneralSecurityException {
        // Load service account credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(googleDriveCredentials.getBytes()))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        // Build the Drive client
        driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, new HttpCredentialsAdapter(credentials))
                .setApplicationName(googleDriveApplicationName).build();
    }

    /**
     * Search for a file by name in Google Drive.
     * 
     * @param fileName Name of the file to search.
     * @return Optional Google Drive File object.
     * @throws IOException              If an error occurs while communicating with
     *                                  the Drive API.
     * @throws GeneralSecurityException If there's a security issue.
     */
    private Optional<File> searchFileByName(String fileName) throws IOException {

        // Query to search file by name
        String query = String.format("'%s' in parents and name = '%s' and trashed = false", googleDriveFolderId, fileName);

        FileList result = driveService.files().list().setQ(query) // Query to filter by file name
                .setFields("files(id, name, mimeType)") // Get file ID, name, and MIME type
                .execute();

        List<File> files = result.getFiles();

        if (files.isEmpty()) {
            return Optional.empty(); // File not found
        }

        return Optional.of(files.get(0)); // Return the first matching file
    }

    private <T> File replaceCsvFileOrders(String fileId, String[] headers, List<Order> orders) throws IOException {

        // Step 1: Write the new data to a temporary CSV file
        java.io.File tempFile = java.io.File.createTempFile("temp_" + fileId, ".csv");
        tempFile.deleteOnExit();

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(tempFile.getPath()))) {

            csvWriter.writeNext(headers);

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
        }

        // Step 2: Re-upload the updated file to Google Drive, replacing the original
        FileContent mediaContent = new FileContent("text/csv", tempFile);
        File updatedFile = driveService.files().update(fileId, null, mediaContent).setFields("id, name, parents, modifiedTime").execute();

        // Delete the temporary file
        tempFile.delete();

        return updatedFile;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    protected void updateOrderFile() {

        final String filename = "Orders.csv";

        LocalDateTime twoMonthsAgo = LocalDateTime.now().minus(2, ChronoUnit.MONTHS);
        List<Order> orders = orderRepository.findByCreatedAtAfter(twoMonthsAgo);

        // Define header
        String[] header = { "id", "customer_name", "sales_rep_name", "profile_description", "unit_type", "pack_size", "price", "quantity", "total_price", "delivery_date",
                "customer_id", "customer_email", "sales_rep_phone", "order_id", "customer_po", "profile_id", "profile_did", "ship_to_id", "ship_to_name", "created_by",
                "created_at", "last_updated_by", "last_updated_at" };

        try {
            Optional<File> fileOptional = searchFileByName(filename);
            if (fileOptional.isPresent()) {
                File file = fileOptional.get();
                replaceCsvFileOrders(file.getId(), header, orders);
            } else {
                logger.error("File with name '" + filename + "' not found.");
                logMessage("File with name '" + filename + "' not found.");
            }
        } catch (IOException ex) {
            logger.error("Error in '" + filename + "': " + ex.getMessage(), ex);
            logMessage("Error in '" + filename + "': " + ex.getMessage());
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    protected void refreshProfileTableFromCsv() {

        final String filename = "Profiles.csv";

        try {
            Optional<File> fileOptional = searchFileByName(filename);

            if (fileOptional.isPresent()) {

                File file = fileOptional.get();

                // Fetch the CSV file content
                InputStream inputStream = driveService.files().get(file.getId()).executeMediaAsInputStream();

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
                            logger.error("Error in '" + filename + "': " + ex.getMessage(), ex);
                            logMessage("Error in '" + filename + "': " + ex.getMessage());
                        }
                    }
                    profileRepository.deleteAllProfilesInBulk();
                    bulkInsert(profiles, "profiles");
                }
            } else {
                logger.error("File with name '" + filename + "' not found.");
                logMessage("File with name '" + filename + "' not found.");
            }

        } catch (IOException | CsvValidationException ex) {
            logger.error("Error in '" + filename + "': " + ex.getMessage(), ex);
            logMessage("Error in '" + filename + "': " + ex.getMessage());
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    protected void refreshInventoryTableFromCsv() {

        final String filename = "Inventory.csv";

        try {
            Optional<File> fileOptional = searchFileByName(filename);

            if (fileOptional.isPresent()) {
                File file = fileOptional.get();

                // Fetch the CSV file content
                InputStream inputStream = driveService.files().get(file.getId()).executeMediaAsInputStream();

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
                            logger.error("Error in '" + filename + "': " + ex.getMessage(), ex);
                            logMessage("Error in '" + filename + "': " + ex.getMessage());
                        }
                    }
                    inventoryRepository.deleteAllInventoriesInBulk();
                    bulkInsert(inventories, "inventories");
                }
            } else {
                logger.error("File with name '" + filename + "' not found.");
                logMessage("File with name '" + filename + "' not found.");
            }

        } catch (IOException | CsvValidationException ex) {
            logger.error("Error in '" + filename + "': " + ex.getMessage(), ex);
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

        final String filename = "Errors.log";

        try {

            Optional<File> fileOptional = searchFileByName(filename);

            if (fileOptional.isPresent()) {
                File file = fileOptional.get();

                InputStream inputStream = driveService.files().get(file.getId()).executeMediaAsInputStream();

                java.io.File tempFile = java.io.File.createTempFile("temp_" + file.getId(), ".csv");
                tempFile.deleteOnExit();

                // Read the current content of the file into a StringBuilder
                StringBuilder currentContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        currentContent.append(line).append(System.lineSeparator());
                    }
                }

                // Prepend the new message to the local copy
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true))) {
                    // Format the time in a readable format (optional)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

                    writer.write(LocalDateTime.now().format(formatter) + ": " + message);
                    writer.newLine(); // Add a new line after the message

                    // Then write the old content
                    writer.write(currentContent.toString());
                }

                // Upload the modified file back to Google Drive
                FileContent mediaContent = new FileContent("text/plain", tempFile);
                driveService.files().update(file.getId(), null, mediaContent).setFields("id, name, parents, modifiedTime").execute();

                tempFile.delete();
            } else {
                logger.error("File with name '" + filename + "' not found.");
            }

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}