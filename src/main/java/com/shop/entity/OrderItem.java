package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //하나의 상품은 여러 주문 상품으로 들어갈 수 있으니. 주문상품 기준으로 다대일 단방향

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //한번의 주문에는 여러개의 상품을 주문할 수 있으니. 다대일 단반향

    private int orderPrice; //주문가격

    private int count; //수량

}
