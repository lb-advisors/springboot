package com.lbadvisors.pffc.profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    List<Profile> findByCustomerId(int customerId);

    @Query("SELECT DISTINCT new com.lbadvisors.pffc.profiles.SalesRepDto(c.salesRepName) FROM Profile c")
    List<SalesRepDto> findDistinctSalesRepNames();

    @Query("SELECT DISTINCT new com.lbadvisors.pffc.profiles.CustomerDto(c.customerId, c.customerName) FROM Profile c where c.salesRepName = :salesRepName")
    List<CustomerDto> findDistinctCustomerIds(@Param("salesRepName") String salesRepName);

}
