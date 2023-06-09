package com.shop.controller;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    //장바구니에에 담기
    @PostMapping("/cart")
    public  @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                               BindingResult bindingResult, Principal principal){
        
        if(bindingResult.hasErrors()){ //cartItemDto 객체 데이터 바인딩시 에러검사

            StringBuilder sb = new StringBuilder();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for(FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long cartItemId;

        try{

            cartItemId = cartService.addCart(cartItemDto, email);

        }catch (Exception e){

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);

    }

    //장바구니 조회
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){

        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());

        model.addAttribute("cartItems", cartDetailDtoList);

        return "cart/cartList";
    }


    // HTTP 메소드에서 PATCH는 요청된 자원의 일부를 업데이트 할때 사용
    // 장바구니 상품 수량 업데이트
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        if(count < 1){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        }else if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);

    }

    //장바구니 상품 삭제
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);

    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for(CartOrderDto cartOrder : cartOrderDtoList){
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);

    }

}
