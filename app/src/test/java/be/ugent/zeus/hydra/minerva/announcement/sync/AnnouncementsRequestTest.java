package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.minerva.common.AbstractMinervaRequestTest;
import be.ugent.zeus.hydra.minerva.common.MinervaRequest;
import be.ugent.zeus.hydra.minerva.course.Course;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class AnnouncementsRequestTest extends AbstractMinervaRequestTest<ApiAnnouncements> {

    private Course course = generate(Course.class);

    @Override
    protected String getRelativePath() {
        return "minerva/announcements.json";
    }

    @Override
    protected MinervaRequest<ApiAnnouncements> getRequest() {
        return new AnnouncementsRequest(RuntimeEnvironment.application, getAccount(), course);
    }
}