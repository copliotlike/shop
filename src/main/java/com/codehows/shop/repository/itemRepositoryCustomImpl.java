package com.codehows.shop.repository;

import com.codehows.shop.Dto.ItemSearchDto;
import com.codehows.shop.constant.ItemSellStatus;
import com.codehows.shop.entity.Item;
import com.codehows.shop.entity.QItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.codehows.shop.entity.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    private BooleanExpression regDtsAfter(String searchDateType) {
        /* saerchDateType 화면 ==> "all", "1d", "1w", "1m", "6m" */
        LocalDateTime now = LocalDateTime.now();

        if(StringUtils.equals(searchDateType, "1d")) {
            now = now.minusDays(1);
        }else if(StringUtils.equals(searchDateType, "1w")) {
            now = now.minusWeeks(1);
        }else if(StringUtils.equals(searchDateType, "1m")) {
            now = now.minusMonths(1);
        }else if(StringUtils.equals(searchDateType, "6m")) {
            now = now.minusMonths(6);
        }else if(StringUtils.equals(searchDateType, "all") || searchDateType == null) {
            return null;
        }

        return item.regTime.after(now);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus itemSellStatus) {
        if(itemSellStatus == null) {
            return null;
        }
        return item.itemSellStatus.eq(itemSellStatus);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        // 무엇을 기준으로(searchBy) 검색할 키워드(searchQuery)
        // searchBy 화면 ==> "itemNm", "createdBy"
        if(searchBy.equals("itemNm")) {
            return item.itemNm.like("%" + searchQuery + "%");
        }else if(searchBy.equals("createdBy")) {
            return item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        /*
        목적 : item 테이블에서 검색 조건에 맞는 결과를 페이지 단위로 조회
        조건 1. searchDateType에 따라 검색 기간 설정
        조건 2. searchSellStatus에 따라 상품 판매 상태(SOLD_OUT, SELL) 설정
        조건 3. searchBy + searchQuery에 따라 검색 키워드 설정
        ==> item_id를 기준으로 내림차순, pageable 기준에 따른 페이징된 결과 반환

        SELECT FROM item
        WHERE 조건1 AND 조건2 AND 조건3
        ORDER BY item_id DESC
        LIMIT, OFFSET . . .

        Page(인터페이스)-PageImpl(구현체)
        PageImpl
        ㄴcontent : List<T>
        ㄴtotalCount : 총 페이지 수
        ㄴnumber : 페이지 번호
        */

        List<Item> content = jpaQueryFactory
                .selectFrom(item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())) // 조건1~3 중 null이 들어가는 경우 ==> 무시



        return null;
    }

}