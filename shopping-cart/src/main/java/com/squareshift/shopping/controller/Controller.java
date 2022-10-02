package com.squareshift.shopping.controller;


import com.squareshift.shopping.requestvo.AddItemRequest;
import com.squareshift.shopping.requestvo.RequestAction;
import com.squareshift.shopping.responsevo.CartResponse;
import com.squareshift.shopping.responsevo.GetCartItemsResponse;
import com.squareshift.shopping.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class Controller {

    @Autowired
    private CartService cartService;

    @PostMapping("/item")
    public CartResponse addItemsToCart(@RequestBody AddItemRequest item)
    {
        return cartService.addItem(item);
    }

    @GetMapping("/items")
    public GetCartItemsResponse getCartItems()
    {
        return cartService.getCartItems();
    }

    @PostMapping("/items")
    public CartResponse emptyCart(@RequestBody RequestAction requestAction)
    {
        return cartService.emptyCartItems(requestAction.getAction());
    }

    @GetMapping("/checkout-value/{postal-code}")
    public CartResponse getCartValue(@PathVariable("postal-code") int postalCode)
    {
        return cartService.getCartValue(postalCode);
    }

}
