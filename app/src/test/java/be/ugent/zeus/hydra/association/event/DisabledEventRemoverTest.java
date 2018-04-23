package be.ugent.zeus.hydra.association.event;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.testing.Utils;
import be.ugent.zeus.hydra.utils.PreferencesUtils;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.association.preference.AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
@RequiresApi(api = Build.VERSION_CODES.N)
public class DisabledEventRemoverTest {

    private List<Event> data;
    private List<Association> associations;

    @Before
    public void setUp() throws IOException {
        Moshi moshi = InstanceProvider.getMoshi();
        data = Utils.readJson(moshi, "all_activities.json",
                Types.newParameterizedType(List.class, Event.class));
        this.associations = Utils.readJson(moshi, "associations.json",
                Types.newParameterizedType(List.class, Association.class));
    }

    @Test
    public void testNoFilter() {

        // Add the associations we want to filter out. For that, we select 10 associations at random from the list.
        List<Association> copy = new ArrayList<>(associations);
        Collections.shuffle(copy);
        Set<String> toRemoveIds = copy.subList(0, 10)
                .stream()
                .map(Association::getInternalName)
                .collect(Collectors.toSet());

        // Add those to the preferences
        PreferencesUtils.addToStringSet(RuntimeEnvironment.application, PREF_ASSOCIATIONS_SHOWING, toRemoveIds);

        // Do the filtering
        DisabledEventRemover filter = new DisabledEventRemover(RuntimeEnvironment.application);
        List<Event> result = filter.apply(data);

        assertTrue(
                "Events that should have been filtered away are still present.",
                result.stream().noneMatch(event -> toRemoveIds.contains(event.getAssociation().getInternalName()))
        );
    }
}