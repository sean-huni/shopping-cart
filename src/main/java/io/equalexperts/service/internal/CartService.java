package io.equalexperts.service.internal;

import io.equalexperts.model.ConsolidatedCart;
import io.equalexperts.model.ProductIn;

public interface CartService {

    ConsolidatedCart validateAndAddToCart(ProductIn productIn);
}
