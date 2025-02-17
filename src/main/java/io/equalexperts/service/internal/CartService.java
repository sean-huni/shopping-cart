package io.equalexperts.service.internal;

import io.equalexperts.model.ProductIn;
import io.equalexperts.view.CartSummaryView;

public interface CartService {

    CartSummaryView validateAndAddToCart(ProductIn productIn);
}
