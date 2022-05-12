/*
 * Copyright (c) 2022 Niko Strijbol
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.wpi.tap.cart;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import androidx.annotation.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.wpi.tap.product.Product;
import be.ugent.zeus.hydra.wpi.tap.product.ProductRequest;

/**
 * @author Niko Strijbol
 */
class CartRequest implements Request<Cart> {
    
    private final Request<List<Product>> productRequest;
    private final Request<CartStorage> existingCartRequest;

    public CartRequest(@NonNull Context context) {
        this.productRequest = new ProductRequest(context);
        this.existingCartRequest = new ExistingCartRequest(context);
    }

    @NonNull
    @Override
    public Result<Cart> execute(@NonNull Bundle args) {
        return productRequest.andThen(existingCartRequest)
                .map(pair -> {
                    Map<Integer, Product> productMap = pair.first.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
                    Cart cart = new Cart();
                    cart.setLastEdited(pair.second.getLastEdited());
                    cart.setProductMap(productMap);
                    List<CartProduct> cartProducts = new ArrayList<>();
                    for (Pair<Integer, Integer> productIdAndAmount: pair.second.getProductIds()) {
                        Product product = productMap.get(productIdAndAmount.first);
                        if (product == null) {
                            continue;
                        }
                        cartProducts.add(CartProduct.fromProduct(product, productIdAndAmount.second));
                    }
                    cart.setOrders(cartProducts);
                    return cart;
                })
                .execute(args);
    }
}
