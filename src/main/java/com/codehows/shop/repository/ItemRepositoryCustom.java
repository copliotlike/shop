package com.codehows.shop.repository;

import com.codehows.shop.Dto.ItemSearchDto;
import com.codehows.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
