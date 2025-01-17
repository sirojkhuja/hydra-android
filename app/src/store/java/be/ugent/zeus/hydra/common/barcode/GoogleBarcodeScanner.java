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

package be.ugent.zeus.hydra.common.barcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.scanner.BarcodeScanner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

/**
 * @author Niko Strijbol
 */
class GoogleBarcodeScanner implements BarcodeScanner {

    @Override
    public boolean needsActivity() {
        return false;
    }

    @Override
    public Intent getActivityIntent(Activity activity) {
        throw new UnsupportedOperationException("This Barcode Scanner does not use an activity.");
    }

    @Override
    public int getRequestCode() {
        throw new UnsupportedOperationException("This Barcode Scanner does not use an activity.");
    }

    @Nullable
    @Override
    public String interpretActivityResult(Intent data, int resultCode) {
        throw new UnsupportedOperationException("This Barcode Scanner does not use an activity.");
    }

    @Override
    public void getBarcode(Context context, Consumer<String> onSuccess, Consumer<Exception> onError) {
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        // Americans...
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_UPC_A)
                .build();
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(context, options);
        scanner.startScan()
                .addOnSuccessListener(barcode -> onSuccess.accept(barcode.getRawValue()))
                .addOnFailureListener(onError::accept);
    }
}
