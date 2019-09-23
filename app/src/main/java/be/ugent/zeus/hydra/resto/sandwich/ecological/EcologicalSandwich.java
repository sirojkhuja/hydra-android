package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

import com.squareup.moshi.Json;
import java9.util.Objects;
import org.threeten.bp.LocalDate;

/**
 * Ecological sandwich of the week.
 */
public final class EcologicalSandwich implements Parcelable {

    private String name;
    private boolean vegan;
    private List<String> ingredients;

    private LocalDate start;
    private LocalDate end;

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public LocalDate getEnd() {
        return end;
    }

    public LocalDate getStart() {
        return start;
    }

    public boolean isVegan() {
        return vegan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringList(this.ingredients);
        dest.writeSerializable(this.start);
        dest.writeSerializable(this.end);
    }

    protected EcologicalSandwich(Parcel in) {
        this.name = in.readString();
        this.ingredients = in.createStringArrayList();
        this.start = (LocalDate) in.readSerializable();
        this.end = (LocalDate) in.readSerializable();
    }

    public static final Creator<EcologicalSandwich> CREATOR = new Creator<EcologicalSandwich>() {
        @Override
        public EcologicalSandwich createFromParcel(Parcel source) {
            return new EcologicalSandwich(source);
        }

        @Override
        public EcologicalSandwich[] newArray(int size) {
            return new EcologicalSandwich[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EcologicalSandwich sandwich = (EcologicalSandwich) o;
        return Objects.equals(name, sandwich.name) &&
                Objects.equals(ingredients, sandwich.ingredients) &&
                Objects.equals(start, sandwich.start) &&
                Objects.equals(end, sandwich.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients, start, end);
    }

    @Override
    public String toString() {
        return name;
    }
}
