/*
 * Copyright (c) 2022 The Hydra authors
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

package be.ugent.zeus.hydra.wpi.tap.order;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import okhttp3.Request;

/**
 * @author Niko Strijbol
 */
public class OrderRequest extends JsonArrayRequest<Order> {

    private final String type;
    private final Context context;

    public OrderRequest(Context context, @Nullable String type) {
        super(context, Order.class);
        this.context = context.getApplicationContext();
        this.type = type;
    }

    public OrderRequest(Context context) {
        this(context, "pending");
    }

    @Override
    protected Request.Builder constructRequest(@NonNull Bundle arguments) {
        Request.Builder builder = super.constructRequest(arguments);
        builder.addHeader("Authorization", "Bearer " + AccountManager.getTapKey(context));
        return builder;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        String suffix = "";
        if (type != null) {
            suffix = "?state=" + type;
        }
        return Endpoints.TAP + "users/" + AccountManager.getUsername(context) + "/orders" + suffix;
    }
}