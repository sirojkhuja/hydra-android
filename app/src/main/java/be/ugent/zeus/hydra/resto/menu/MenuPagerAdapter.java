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

package be.ugent.zeus.hydra.resto.menu;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.SingleDayFragment;

/**
 * This class provides the tabs in the resto activity.
 *
 * @author Niko Strijbol
 */
class MenuPagerAdapter extends FragmentStateAdapter {
    private static final int LEGEND = -63;

    private List<RestoMenu> data = Collections.emptyList();

    public MenuPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<RestoMenu> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public boolean hasData() {
        return !data.isEmpty();
    }

    @Nullable
    LocalDate getTabDate(int position) {
        return position == 0 ? null : data.get(position - 1).getDate();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LegendFragment();
        } else {
            return SingleDayFragment.newInstance(data.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        if (hasData()) {
            return data.size() + 1;
        } else {
            return data.size();
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == 0) {
            return LEGEND;
        } else {
            RestoMenu menu = data.get(position - 1);
            return menu.hashCode();
        }
    }

    @Override
    public boolean containsItem(long itemId) {
        if (itemId == LEGEND) {
            return true;
        }

        List<Long> data = this.data.stream().map(restoMenu -> (long) restoMenu.hashCode()).collect(Collectors.toList());
        return data.contains(itemId);
    }
}
