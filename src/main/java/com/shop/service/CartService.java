package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    
    private final MemberRepository memberRepository;
    
    private final CartRepository cartRepository;
    
    private final CartItemRepository cartItemRepository;
    

    public Long addCart(CartItemDto cartItemDto, String email){

        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new); //장바구니에 담을 상품 엔티티 조회

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId()); //로그인한 회원의 장바구니 엔티티조회

        if(cart == null){ //처음으로 장바구니에 담을 경우 -> 회원 장바구니 엔티티 생성
            
            cart = Cart.createCart(member);
            cartRepository.save(cart);
            
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId()); //현재 장바구니에 이미 존재하는 조회
        
        if(savedCartItem != null){ //이미 장바구니에 존재하는 상품
            
            savedCartItem.addCount(cartItemDto.getCount()); //기존상품수량에서 담을 수량 만큼 +

            return savedCartItem.getId();
            
        }else{ //장바구니에 존재하지 않는 상품
            
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem); //장바구니에 상품추가

            return cartItem.getId();
        }
    }

    //장바구니 상품 조회
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());

        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;

    }


}
