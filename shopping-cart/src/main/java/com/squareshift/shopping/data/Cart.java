package com.squareshift.shopping.data;

import com.squareshift.shopping.data.CartItem;
import com.squareshift.shopping.requestvo.AddItemRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    private List<CartItem> cartItems;
    private BigDecimal cartValue;
    private BigDecimal cartWeight;
}
