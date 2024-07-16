package com.lbadvisors.pffc.controllers.profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lbadvisors.pffc.controllers.company.CompanyGetDto;
import com.lbadvisors.pffc.controllers.customers.CustomerGetDto;
import com.lbadvisors.pffc.controllers.sales_reps.SalesRepGetDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    List<Profile> findByCustomerId(int customerId);

    Optional<Profile> findById(int id);

    @Query("SELECT DISTINCT new com.lbadvisors.pffc.controllers.company.CompanyGetDto(c.companyId, c.companyName) FROM Profile c")
    List<CompanyGetDto> findDistinctCompanies();

    @Query("SELECT DISTINCT new com.lbadvisors.pffc.controllers.sales_reps.SalesRepGetDto(c.salesRepName) FROM Profile c where c.companyId = :companyId ORDER BY c.salesRepName")
    List<SalesRepGetDto> findDistinctSalesRepNames(@Param("companyId") Integer companyId);

    @Query("SELECT DISTINCT new com.lbadvisors.pffc.controllers.customers.CustomerGetDto(c.customerId, c.customerName) FROM Profile c where c.companyId = :companyId and c.salesRepName = :salesRepName ORDER BY c.customerName")
    List<CustomerGetDto> findDistinctCustomers(@Param("companyId") Integer companyId,
            @Param("salesRepName") String salesRepName);

}
