/*
 * Copyright (c) 2021 The Hydra authors
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

package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

import com.squareup.moshi.Json;

/**
 * Created by feliciaan on 04/02/16.
 */
@SuppressWarnings("WeakerAccess")
public final class RegularSandwich implements Parcelable {
    
    private String name;
    private List<String> ingredients;
    @Json(name = "price_small")
    private String priceSmall;
    @Json(name = "price_medium")
    private String priceMedium;

    @SuppressWarnings("unused")
    public RegularSandwich() {

    }

    public RegularSandwich(Parcel in) {
        this.name = in.readString();
        this.ingredients = in.createStringArrayList();
        this.priceSmall = in.readString();
        this.priceMedium = in.readString();
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getPriceSmall() {
        return priceSmall;
    }

    public String getPriceMedium() {
        return priceMedium;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringList(this.ingredients);
        dest.writeString(this.priceSmall);
        dest.writeString(this.priceMedium);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularSandwich sandwich = (RegularSandwich) o;
        return Objects.equals(name, sandwich.name) &&
                Objects.equals(ingredients, sandwich.ingredients) &&
                Objects.equals(priceSmall, sandwich.priceSmall) &&
                Objects.equals(priceMedium, sandwich.priceMedium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients, priceSmall, priceMedium);
    }

    @Override
    public String toString() {
        return name;
    }

    public static final Parcelable.Creator<RegularSandwich> CREATOR = new Parcelable.Creator<RegularSandwich>() {
        @Override
        public RegularSandwich createFromParcel(Parcel source) {
            return new RegularSandwich(source);
        }

        @Override
        public RegularSandwich[] newArray(int size) {
            return new RegularSandwich[size];
        }
    };
}
