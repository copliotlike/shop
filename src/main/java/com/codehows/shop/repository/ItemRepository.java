package com.codehows.shop.repository;

import com.codehows.shop.entity.Item;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByItemNm(String itemNm);
    //Select * From item where item_nm = ?

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail, Sort sort, Limit limit);
    //select * from item where item_nm =? OR item_Detail = ?;

    List<Item> findByPriceLessThan(Integer price);
    //SELECT * FROM item WHERE price < ?;

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    //SELECT * FROM item Where price < ? : order by price desc;

    @Query("select i from Item i where i.itemDetail like %:itemDatil% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDatil") String itemDatil);

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
