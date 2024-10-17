package com.lbadvisors.pffc.controllers.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lbadvisors.pffc.controllers.inventory.Inventory;
import com.lbadvisors.pffc.controllers.inventory.InventoryRepository;
import com.lbadvisors.pffc.controllers.orders.OrderRepository;
import com.lbadvisors.pffc.controllers.orders.Order;
import com.lbadvisors.pffc.controllers.profiles.Profile;
import com.lbadvisors.pffc.controllers.profiles.ProfileRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ScheduledJobs {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    OneDriveService oneDriveService;

    private String oneDriveFolderName;

    public ScheduledJobs(@Value("${onedrive.folder.name}") String oneDriveFolderName) {
        this.oneDriveFolderName = oneDriveFolderName;
    }

    @Scheduled(cron = "0 */2 * * * ?")
    public void updateOrderFile() {

        final String filename = oneDriveFolderName + "/" + "Orders.csv";

        LocalDateTime twoMonthsAgo = LocalDateTime.now().minus(2, ChronoUnit.MONTHS);
        List<Order> orders = orderRepository.findByCreatedAtAfter(twoMonthsAgo);

        // Define header
        String[] header = { "id", "customer_name", "sales_rep_name", "profile_description", "unit_type", "pack_size", "price", "quantity", "total_price", "delivery_date",
                "customer_id", "customer_email", "sales_rep_phone", "order_id", "customer_po", "profile_id", "profile_did", "ship_to_id", "ship_to_name", "company_id",
                "created_by", "created_at", "last_updated_by", "last_updated_at" };

        StringWriter writer = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(header);

            // Write user data to CSV
            for (Order order : orders) {
                String[] data = { (order.getId() != null) ? String.valueOf(order.getId()) : null, order.getCustomerName(), order.getSalesRepName(), order.getProfileDescription(),
                        order.getUnitType(), (order.getPackSize() != null) ? String.valueOf(order.getPackSize()) : null,
                        (order.getPrice() != null) ? String.valueOf(order.getPrice()) : null, (order.getQuantity() != null) ? String.valueOf(order.getQuantity()) : null,
                        (order.getTotalPrice() != null) ? String.valueOf(order.getTotalPrice()) : null,
                        (order.getDeliveryDate() != null) ? String.valueOf(order.getDeliveryDate()) : null,
                        (order.getCustomerId() != null) ? String.valueOf(order.getCustomerId()) : null, order.getCustomerEmail(), order.getSalesRepPhone(),
                        String.valueOf(order.getOrderId()), order.getCustomerPo(), (order.getProfileId() != null) ? String.valueOf(order.getProfileId()) : null,
                        (order.getProfileDid() != null) ? String.valueOf(order.getProfileDid()) : null, (order.getShipToId() != null) ? String.valueOf(order.getShipToId()) : null,
                        order.getShipToName(), (order.getCompanyId() != null) ? String.valueOf(order.getCompanyId()) : null, order.getCreatedBy(),
                        (order.getCreatedAt() != null) ? String.valueOf(order.getCreatedAt()) : null, order.getLastUpdatedBy(),
                        (order.getLastUpdatedAt() != null) ? String.valueOf(order.getLastUpdatedAt()) : null };
                csvWriter.writeNext(data);
            }

            oneDriveService.uploadFile(filename, writer.toString());

        } catch (IOException ex) {
            oneDriveService.logErrorMessage("Error in '" + filename + "': " + ex.getMessage());
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    protected void refreshProfileTableFromCsv() {

        final String filename = oneDriveFolderName + "/" + "Profiles.csv";

        try {

            // Fetch the CSV file content
            // Read the CSV file
            try (InputStream inputStream = oneDriveService.getFileContent(filename);
                    CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

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
                        oneDriveService.logErrorMessage("Error in '" + filename + "': " + ex.getMessage());
                    }
                }
                profileRepository.deleteAllProfilesInBulk();
                bulkInsert(profiles, "profiles");
            }
        } catch (IOException | CsvValidationException ex) {
            oneDriveService.logErrorMessage("Error in '" + filename + "': " + ex.getMessage());
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    protected void refreshInventoryTableFromCsv() {

        final String filename = oneDriveFolderName + "/" + "Inventory.csv";

        try {

            // Fetch the CSV file content
            // Read the CSV file
            try (InputStream inputStream = oneDriveService.getFileContent(filename);
                    CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

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
                        oneDriveService.logErrorMessage("Error in '" + filename + "': " + ex.getMessage());
                    }
                }
                inventoryRepository.deleteAllInventoriesInBulk();
                bulkInsert(inventories, "inventories");
            }

        } catch (IOException | CsvValidationException ex) {
            oneDriveService.logErrorMessage("Error in '" + filename + "': " + ex.getMessage());
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
}