package be.ugent.zeus.hydra.common;

import android.net.Uri;

import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.news.NewsArticle;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.testing.RobolectricUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class ArticleViewerTest {

    @Test
    public void viewArticleCustomTabsUGent() {
        ActivityHelper helper = mock(ActivityHelper.class);
        NewsArticle newsItem = generate(NewsArticle.class);
        ArticleViewer.viewArticle(RobolectricUtils.getActivityContext(), newsItem, helper);
        verify(helper, times(1)).openCustomTab(any(Uri.class));
    }

    @Test
    public void viewArticleCustomTabsSchamper() {
        ActivityHelper helper = mock(ActivityHelper.class);
        Article newsItem = generate(Article.class);
        ArticleViewer.viewArticle(RobolectricUtils.getActivityContext(), newsItem, helper);
        verify(helper, times(1)).openCustomTab(any(Uri.class));
    }
}
