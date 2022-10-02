package com.squareshift.shopping.responsevo;

import com.squareshift.shopping.data.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductResponse {

    private String status;
    private String message;
    private Product product;
}
