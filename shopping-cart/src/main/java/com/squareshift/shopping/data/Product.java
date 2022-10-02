package com.squareshift.shopping.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private int id;
    private String name;
    private BigDecimal price;
    private String description;
    private String category;
    private String image;
    private String discount_percentage;
    private String weight_in_grams;
}
