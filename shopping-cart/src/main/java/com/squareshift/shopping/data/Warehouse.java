package com.squareshift.shopping.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {

    private String status;
    private BigDecimal distance_in_kilometers;
}
