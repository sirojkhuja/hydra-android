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

package be.ugent.zeus.hydra.resto.extrafood;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import org.jetbrains.annotations.NotNull;

/**
 * This provides the tabs in a minerva course.
 *
 * @author Niko Strijbol
 */
class ExtraFoodPagerAdapter extends FragmentStateAdapter implements TabLayoutMediator.TabConfigurationStrategy {

    ExtraFoodPagerAdapter(FragmentActivity owner) {
        super(owner);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return FoodFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
        @StringRes int string;
        switch (position) {
            case 0:
                string = R.string.resto_extra_breakfast;
                break;
            case 1:
                string = R.string.resto_extra_desserts;
                break;
            case 2:
                string = R.string.resto_extra_drinks;
                break;
            default:
                throw new AdapterOutOfBoundsException(position, getItemCount());
        }

        tab.setText(string);
    }
}
