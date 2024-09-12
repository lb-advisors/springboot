package com.lbadvisors.pffc.controllers.database;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lbadvisors.pffc.controllers.orders.Order;
import com.lbadvisors.pffc.controllers.orders.OrderRepository;
import com.lbadvisors.pffc.controllers.profiles.Profile;
import com.lbadvisors.pffc.controllers.profiles.ProfileRepository;
import com.lbadvisors.pffc.exception.ControllerAdvisor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProfileRepository profileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public void writeOrdersToCsv(PrintWriter writer) throws IOException {
        List<Order> orders = orderRepository.findAll();

        // Create CSVWriter instance
        CSVWriter csvWriter = new CSVWriter(writer);

        // Define header
        String[] header = { "id", "customer_name", "sales_rep_name", "profile_description", "unit_type", "pack_size", "price", "quantity", "total_price", "delivery_date",
                "customer_id", "customer_email", "sales_rep_phone", "order_id", "customer_po", "profile_id", "profile_did", "ship_to_id", "ship_to_name", "created_by",
                "created_at", "last_updated_by", "last_updated_at" };

        csvWriter.writeNext(header);

        // Write user data to CSV
        for (Order order : orders) {
            String[] data = { String.valueOf(order.getId()), order.getCustomerName(), order.getSalesRepName(), order.getProfileDescription(), order.getUnitType(),
                    String.valueOf(order.getPackSize()), String.valueOf(order.getPrice()), String.valueOf(order.getQuantity()), String.valueOf(order.getTotalPrice()),
                    String.valueOf(order.getDeliveryDate()), String.valueOf(order.getCustomerId()), order.getCustomerEmail(), order.getSalesRepPhone(),
                    String.valueOf(order.getOrderId()), order.getCustomerPo(), String.valueOf(order.getProfileDid()), String.valueOf(order.getShipToId()), order.getShipToName(),
                    order.getShipToName(), order.getCreatedBy(), String.valueOf(order.getCreatedAt()), order.getLastUpdatedBy(), String.valueOf(order.getLastUpdatedAt()) };
            csvWriter.writeNext(data);
        }

        // Close CSVWriter
        csvWriter.close();
    }

    @Transactional
    public void parseProfilesFromCsvAndSaveData(MultipartFile file) throws IOException, CsvValidationException {
        List<Profile> profiles = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            csvReader.readNext(); // Reads and discards the first line (header)

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
                    // profile.setCustomerName(values[6]);
                    profile.setCustomerName("CAFÉ STELLA");
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

                    logger.error(values[6]);
                    logger.error("CAFÉ STELLA");

                    profile.setCompanyName(values[16]);
                    if (values[17].length() > 0)
                        profile.setCompanyId(Integer.parseInt(values[17]));

                    profiles.add(profile);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        profileRepository.deleteAllProfilesInBulk();
        bulkInsert(profiles);
    }

    @Transactional
    public void bulkInsert(List<Profile> profiles) {
        int batchSize = 50;
        for (int i = 0; i < profiles.size(); i++) {
            entityManager.persist(profiles.get(i));

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
