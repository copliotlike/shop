package com.codehows.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import com.codehows.shop.entity.Order;

public interface OrderRepository extends JpaRepository<Order,Long> {

}
