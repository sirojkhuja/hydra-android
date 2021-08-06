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

package be.ugent.zeus.hydra.resto.sandwich;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.resto.sandwich.ecological.EcologicalFragment;
import be.ugent.zeus.hydra.resto.sandwich.regular.RegularFragment;

/**
 * This provides the tabs in the sandwich overview.
 *
 * @author Niko Strijbol
 */
class SandwichPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    SandwichPagerAdapter(FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context.getApplicationContext();
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RegularFragment();
            case 1:
                return new EcologicalFragment();
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }
    }

    @Override
    @NonNull
    public CharSequence getPageTitle(int position) {
        @StringRes int string;
        switch (position) {
            case 0:
                string = R.string.resto_main_view_sandwiches_regular;
                break;
            case 1:
                string = R.string.resto_main_view_sandwiches_ecological;
                break;
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }

        return context.getString(string);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }
}
