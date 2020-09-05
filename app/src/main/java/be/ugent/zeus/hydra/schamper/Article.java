package be.ugent.zeus.hydra.schamper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import com.squareup.moshi.Json;

/**
 * A Schamper article.
 *
 * @author Niko Strijbol
 * @author Feliciaan
 * @see <a href="https://schamper.ugent.be">The Schamper website</a>
 */
@SuppressWarnings("unused")
public final class Article implements Parcelable, ArticleViewer.Article {
    
    private static final Pattern IMAGE_REPLACEMENT = Pattern.compile("/regulier/", Pattern.LITERAL);
    private String title;
    private String link;
    @Json(name = "pub_date")
    private OffsetDateTime pubDate;
    private String author;
    private String body;
    private String image;
    private String category;
    private String intro;
    @Json(name = "category_color")
    private String categoryColour;

    public Article() {
    }

    private Article(Parcel in) {
        title = in.readString();
        link = in.readString();
        pubDate = DateTypeConverters.toOffsetDateTime(in.readString());
        author = in.readString();
        body = in.readString();
        image = in.readString();
        category = in.readString();
        intro = in.readString();
        categoryColour = in.readString();
    }

    static String getLargeImage(String url) {
        return IMAGE_REPLACEMENT.matcher(url).replaceAll(Matcher.quoteReplacement("/preview/"));
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getLink() {
        return link;
    }

    public OffsetDateTime getPubDate() {
        return pubDate;
    }

    public LocalDateTime getLocalPubDate() {
        return DateUtils.toLocalDateTime(getPubDate());
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public String getCategory() {
        return category;
    }

    public String getIntro() {
        return intro;
    }

    public String getImage() {
        return image;
    }

    public String getCategoryColour() {
        return categoryColour;
    }

    public boolean hasCategoryColour() {
        return !TextUtils.isEmpty(categoryColour);
    }

    public String getLargeImage() {
        if (getImage() != null) {
            return getLargeImage(getImage());
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(link, article.link) &&
                Objects.equals(pubDate, article.pubDate);
    }

    public String getIdentifier() {
        return link + pubDate.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(link, pubDate);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(pubDate));
        dest.writeString(author);
        dest.writeString(body);
        dest.writeString(image);
        dest.writeString(category);
        dest.writeString(intro);
        dest.writeString(categoryColour);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
