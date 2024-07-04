package com.lbadvisors.pffc.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = "SELECT NEXTVAL('order_id_seq')", nativeQuery = true)
    Integer getNextOrderIdSequenceValue();

}
