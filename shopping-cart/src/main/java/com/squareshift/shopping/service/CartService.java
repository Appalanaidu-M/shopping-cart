package com.squareshift.shopping.service;

import com.squareshift.shopping.data.CartItem;
import com.squareshift.shopping.config.Config;
import com.squareshift.shopping.data.Warehouse;
import com.squareshift.shopping.data.Cart;
import com.squareshift.shopping.requestvo.AddItemRequest;
import com.squareshift.shopping.data.Product;
import com.squareshift.shopping.responsevo.CartResponse;
import com.squareshift.shopping.responsevo.GetCartItemsResponse;
import com.squareshift.shopping.responsevo.GetProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CartService {

    @Autowired
    private RestTemplate restTemplate;

    private Cart cart;

    private Map<Integer, BigDecimal> warehouseMap = new HashMap<>();

    private Map<Integer, Product> productMap = new HashMap<>();

    @Autowired
    private Map<Config, BigDecimal> getShippingCost;

    public CartResponse addItem(AddItemRequest item) {
        CartResponse addItemResponse = new CartResponse();
        try {
            if(null == cart)
            {
                cart = new Cart();
                cart.setCartItems(new ArrayList<>());
            }
            GetProductResponse productResponse = restTemplate.getForObject("https://e-commerce-api-recruitment.herokuapp.com/product/"+item.getProduct_id(), GetProductResponse.class);
            System.out.println("Response Entity: "+productResponse);
            cart.getCartItems().add(new CartItem(item.getProduct_id(), productResponse.getProduct().getDescription(), item.getQuatity()));
            productMap.put(productResponse.getProduct().getId(), productResponse.getProduct());
            addItemResponse.setStatus("success");
            addItemResponse.setMessage("Item has been added to cart");
        }
        catch (HttpClientErrorException exception)
        {
            addItemResponse.setStatus("error");
            addItemResponse.setMessage("Valid product id range is 100 to 110");
        }
        return addItemResponse;
    }

    public GetCartItemsResponse getCartItems() {
        GetCartItemsResponse cartItemsResponse = new GetCartItemsResponse();
        try
        {
            if(null != cart && cart.getCartItems().size() > 0)
            {
                cartItemsResponse.setStatus("success");
                cartItemsResponse.setMessage("Item available in cart");
                cartItemsResponse.setItems(cart.getCartItems());
            }
            else
            {
                cartItemsResponse.setStatus("success");
                cartItemsResponse.setMessage("Cart is Empty");
            }

        }
        catch (Exception exception)
        {
            cartItemsResponse.setStatus("500");
            cartItemsResponse.setMessage("Internal Server Error");
        }
        return cartItemsResponse;
    }


    public CartResponse emptyCartItems(String action) {
        CartResponse cartResponse = new CartResponse();
        if(null != action && !action.isEmpty() && action.equals("empty_cart"))
        {
            cart = null;
            cartResponse.setStatus("success");
            cartResponse.setMessage("All items have been removed from the cart !");
        }
        else
        {
            cartResponse.setStatus("400");
            cartResponse.setMessage("Bad request");
        }
        return cartResponse;
    }

    public CartResponse getCartValue(int postalCode) {
        CartResponse cartResponse = new CartResponse();
        if(null == cart)
            return new CartResponse("error", "Cart is Empty");
        if(null == warehouseMap.get(postalCode)) {
            if(null == getWarehouseDistance(postalCode))
                return new CartResponse("error", "Invalid postal code, valid ones are 465535 to 465545");
        }
        BigDecimal shippingDistance = warehouseMap.get(postalCode);
        log.info("shippingDistance: "+shippingDistance);
        getItemCost();
        BigDecimal finalCartValue = getCartValueWithShipping(shippingDistance);
        log.info("finalCartValue: "+finalCartValue);
        cartResponse.setStatus("success");
        cartResponse.setMessage("Total value of your shopping cart is - $"+finalCartValue.setScale(2, RoundingMode.HALF_UP));
        return cartResponse;
    }

    private BigDecimal getWarehouseDistance(int postalCode)
    {
        try
        {
            Warehouse warehouse = restTemplate.getForObject("https://e-commerce-api-recruitment.herokuapp.com/warehouse/distance?postal_code="+postalCode, Warehouse.class);
            warehouseMap.put(postalCode, warehouse.getDistance_in_kilometers());
            return warehouse.getDistance_in_kilometers();
        }
        catch (Exception exception)
        {
            System.out.println(" Exception in getWarehouseDistance: "+exception);
        }
        return null;
    }

    private BigDecimal getCartValueWithShipping(BigDecimal shippingDistance)
    {
        log.info("getShippingCost : "+getShippingCost);
        Config config = getConfigKeyByDistance(shippingDistance);
        if(null != config)
        {
            BigDecimal shippingCost = getShippingCost.get(config);
            cart.setCartValue(cart.getCartValue().add(shippingCost));
            log.info("shippingCost : "+shippingCost);
        }
        return cart.getCartValue();
    }

    private void getItemCost()
    {
        for(CartItem cartItem : cart.getCartItems())
        {
            Product cartProduct = productMap.get(cartItem.getProduct_id());
            BigDecimal itemCost = cartProduct.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            double weightInKg = Double.valueOf(cartProduct.getWeight_in_grams())/1000;
            BigDecimal weight = new BigDecimal(weightInKg).multiply(new BigDecimal(cartItem.getQuantity()));
            log.info("Item_Cost: "+itemCost);
            itemCost = getDiscountedPrice(itemCost, cartProduct.getDiscount_percentage());
            log.info("Discounted_Item_Cost: "+itemCost);
            if(null == cart.getCartValue()) {
                cart.setCartValue(new BigDecimal(0));
            }
            if(null == cart.getCartWeight()) {
                cart.setCartWeight(new BigDecimal(0));
            }
            cart.setCartValue(cart.getCartValue().add(itemCost));
            cart.setCartWeight(cart.getCartWeight().add(weight));
            log.info("Item_Weight: "+weight);
        }
        log.info("Cart_Weight: "+cart.getCartWeight());
        log.info("Cart_Value: "+cart.getCartValue());
    }

    private BigDecimal getDiscountedPrice(BigDecimal itemCost, String discount)
    {
        BigDecimal discountValue = itemCost.multiply(new BigDecimal(Double.valueOf(discount)/100));
        log.info("discountValue : "+discountValue);
        return itemCost.subtract(discountValue);
    }

    private Config getConfigKeyByDistance(BigDecimal distance)
    {
        int weightRank = getWeightRank();
        int distanceRank = getDistanceRank(distance);
        if(weightRank == 0 || distanceRank == 0)
        {
            return null;
        }
        return new Config(distanceRank, weightRank);
    }

    private int getWeightRank()
    {
        if(isInClosedRange(cart.getCartWeight(), new BigDecimal(0), new BigDecimal(2)))
        {
            return 1;
        }
        else if (isInClosedRange(cart.getCartWeight(), new BigDecimal(2), new BigDecimal(5)))
        {
            return 2;
        }
        else if (isInClosedRange(cart.getCartWeight(), new BigDecimal(5), new BigDecimal(20)))
        {
            return 3;
        }
        else {
            return 4;
        }
    }

    private int getDistanceRank(BigDecimal distance)
    {
        if(isInClosedRange(distance, new BigDecimal(0), new BigDecimal(5)))
        {
            return 1;
        }
        else if (isInClosedRange(distance, new BigDecimal(5), new BigDecimal(20)))
        {
            return 2;
        }
        else if (isInClosedRange(distance, new BigDecimal(20), new BigDecimal(50)))
        {
            return 3;
        }
        else if (isInClosedRange(distance, new BigDecimal(50), new BigDecimal(500)))
        {
            return 4;
        }
        else if (isInClosedRange(distance, new BigDecimal(500), new BigDecimal(800)))
        {
            return 5;
        }
        else
        {
            return 6;
        }
    }

    private boolean isInClosedRange(BigDecimal number, BigDecimal lowerBound, BigDecimal upperBound) {
        if(number.compareTo(lowerBound) >=0 && number.compareTo(upperBound) < 0)
        {
            return true;
        }
        return false;
    }
}
