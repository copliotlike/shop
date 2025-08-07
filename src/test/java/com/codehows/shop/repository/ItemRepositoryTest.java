package com.codehows.shop.repository;

import com.codehows.shop.constant.ItemSellStatus;
import com.codehows.shop.entity.Item;
import com.codehows.shop.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.codehows.shop.entity.Item.savedItem;
import static com.codehows.shop.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    public void createDummyItems() {
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
            System.out.println(savedItem.toString());
        }

    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        createDummyItems();

        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("상품명, 상세설명 or 테스트")
    public void findByItemNmOritemDetailTest() {
        createDummyItems();

        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByItemNmLessThanTest() {
        createDummyItems();

        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("가격 LessThan orderby 테스트")
    public void findByItemNmLessThanOrderByTest() {
        createDummyItems();

        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        createDummyItems();

        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test @DisplayName("@Query를 이용한 상품 조회 테스트2")
    public void findByItemDetailByNative(){
        createDummyItems();

        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        createDummyItems();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());
        List<Item> itemList = query.fetch();
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){
        for(int i = 0; i < 5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i =6; i<=10;i++ ){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){
        createDummyItems();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem qItem = item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10_003;
        String itemSellStat = "SELL";
        int pageNum = 1;

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

//        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
//            booleanBuilder.and(qItem.itemSellStatus.eq(ItemSellStatus.SELL));
//        }

        if(itemSellStat.equals("SELL")){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(pageNum - 1, 5);
        /*

         */
//        Page<Item> itemPagingResult =
//                itemRepository.findAll(booleanBuilder, pageable);
//        System.out.println("total element :" + itemPagingResult. getTotalElements());
//
//        List<Item> resultItemList = itemPagingResult.getContent();


        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAQuery<Item> baseQuery = queryFactory.selectFrom(item);
        /*
        select * from item
         */

        JPAQuery<Item> conditionedQuery = baseQuery.where(booleanBuilder);
        /*
        select * from item
        where itemDetail like ?
        and price > ?
        and item_sell_status = "SELL" <<-- 조건부;
         */

        JPAQuery<Item> pagedQuery = conditionedQuery
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        /*
        select * from item
        where itemDetail LIKE?
        and Price > ?
        and item_sell_status = "Sell" <<-- 조건부
        Order by id DESC
        Limit 5 OFFSET ?
         */

        List<Item> contents = pagedQuery.fetch();
        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(item)
                .where(booleanBuilder)
                .fetchOne();
        /* select count(*) from item */
        Page<Item> result = new PageImpl<>(contents, pageable, totalCount);

        System.out.println("총 컨텐츠 요소의 수 : " + result.getTotalElements());
        System.out.println("조회가능한 총 페이지 수 : " + result.getTotalPages());

        List<Item> items = result.getContent();
        for(Item item : items){
            System.out.println(item);
        }


    }

}