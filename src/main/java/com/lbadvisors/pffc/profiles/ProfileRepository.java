package com.lbadvisors.pffc.profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbadvisors.pffc.customers.CustomerGetDto;
import com.lbadvisors.pffc.sales_reps.SalesRepGetDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    List<Profile> findByCustomerId(int customerId);

    Optional<Profile> findById(int id);

    @Query("SELECT DISTINCT new com.lbadvisors.pffc.sales_reps.SalesRepGetDto(c.salesRepName) FROM Profile c")
    List<SalesRepGetDto> findDistinctSalesRepNames();

    @Query("SELECT DISTINCT new com.lbadvisors.pffc.customers.CustomerGetDto(c.customerId, c.customerName) FROM Profile c where c.salesRepName = :salesRepName")
    List<CustomerGetDto> findDistinctCustomerIds(@Param("salesRepName") String salesRepName);

}
