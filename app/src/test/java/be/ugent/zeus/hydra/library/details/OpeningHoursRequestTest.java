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

package be.ugent.zeus.hydra.library.details;

import java.io.IOException;
import java.util.List;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.list.LibraryList;
import com.squareup.moshi.JsonAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertNotNull;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class OpeningHoursRequestTest extends AbstractJsonRequestTest<List<OpeningHours>> {

    @Override
    protected String getRelativePath() {
        return "library_hours.json";
    }

    @Override
    protected OpeningHoursRequest getRequest() {
        try {
            return new OpeningHoursRequest(context, getRandomConstLibrary());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLibraryCode() throws IOException {
        Library library = getRandomConstLibrary();
        OpeningHoursRequest request = getRequest();
        assertThat(request.getAPIUrl(), endsWith("libraries/" + library.getCode() + "/calendar.json"));
    }

    private Library getRandomConstLibrary() throws IOException {
        JsonAdapter<LibraryList> listAdapter = moshi.adapter(LibraryList.class);
        LibraryList list = listAdapter.fromJson(readData(getResourceFile("all_libraries.json")));
        assertNotNull(list);
        assertNotNull(list.getLibraries());
        return list.getLibraries().get(0);
    }
}