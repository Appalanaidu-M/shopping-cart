package com.squareshift.shopping.responsevo;

import com.squareshift.shopping.data.CartItem;
import com.squareshift.shopping.data.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCartItemsResponse {

    private String status;
    private String message;
    private List<CartItem> items;
}
