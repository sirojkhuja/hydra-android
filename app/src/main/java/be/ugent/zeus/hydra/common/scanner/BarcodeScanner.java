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

package be.ugent.zeus.hydra.common.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

/**
 * Ask some service to scan for barcodes.
 * 
 * TODO: this is an ugly interface.
 * 
 * @author Niko Strijbol
 */
public interface BarcodeScanner {
    /**
     * If this barcode scanner needs to launch an activity or not.
     */
    boolean needsActivity();

    /**
     * Get an activity to launch, which will give the barcode
     * as a result.
     */
    Intent getActivityIntent(Activity activity);

    /**
     * @return Get the request code to use when launching an activity.
     */
    int getRequestCode();

    /**
     * Get the barcode from the activity launch from the intent from
     * {@link #getActivityIntent(Activity)}.
     * 
     * @param data The result data.
     *             
     * @return The barcode, or null.
     */
    @Nullable
    String interpretActivityResult(Intent data, int resultCode);

    /**
     * Get a barcode without activity.
     * 
     * Implementations should optimize, if possible, for scanning product barcodes.
     * This includes EAN/UPC codes.
     */
    void getBarcode(Context context, Consumer<String> onSuccess, Consumer<Exception> onError);
}
