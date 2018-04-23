package be.ugent.zeus.hydra.association.news.list;

import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.news.UgentNewsItem;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.threeten.bp.OffsetDateTime;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class NewsItemViewHolderTest {

    private ActivityHelper helper;

    @Before
    public void setUp() {
        helper = mock(ActivityHelper.class);
    }

    @Test
    public void populate() {
        View view = inflate(R.layout.item_news);
        NewsItemViewHolder viewHolder = new NewsItemViewHolder(view, helper);
        UgentNewsItem newsItem = generate(UgentNewsItem.class);
        viewHolder.populate(newsItem);

        assertTextIs(newsItem.getTitle(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.info));
        assertNotEmpty(view.findViewById(R.id.article_excerpt));

        assertTrue(view.hasOnClickListeners());
    }

    @Test
    public void populateVariant() {
        View view = inflate(R.layout.item_news);
        NewsItemViewHolder viewHolder = new NewsItemViewHolder(view, helper);
        UgentNewsItem newsItem = generate(UgentNewsItem.class, "description");
        newsItem.setModified(OffsetDateTime.now().plusDays(10));
        viewHolder.populate(newsItem);

        assertTextIs(newsItem.getTitle(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.info));
        assertNotEmpty(view.findViewById(R.id.article_excerpt));

        assertTrue(view.hasOnClickListeners());
    }
}