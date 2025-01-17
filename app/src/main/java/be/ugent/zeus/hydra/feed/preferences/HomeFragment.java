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

package be.ugent.zeus.hydra.feed.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.EventObserver;
import be.ugent.zeus.hydra.common.ui.PreferenceFragment;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;

/**
 * Settings about the home feed.
 *
 * @author Niko Strijbol
 */
public class HomeFragment extends PreferenceFragment {

    public static final String PREF_DATA_SAVER = "pref_home_feed_save_data";
    public static final boolean PREF_DATA_SAVER_DEFAULT = false;
    private static final String PREF_RESTO_KINDS = "pref_feed_resto_kinds";
    private static final String PREF_RESTO_KINDS_DEFAULT = FeedRestoKind.ALL;
    private DeleteViewModel viewModel;

    @MenuTable.DisplayKind
    public static int getFeedRestoKind(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        @FeedRestoKind
        String value = pref.getString(PREF_RESTO_KINDS, PREF_RESTO_KINDS_DEFAULT);

        switch (value) {
            case FeedRestoKind.SOUP:
                return MenuTable.DisplayKind.SOUP;
            case FeedRestoKind.MAIN:
                return MenuTable.DisplayKind.MAIN;
            case FeedRestoKind.ALL:
            default:
                // Don't show vegetables.
                return MenuTable.DisplayKind.ALL & ~MenuTable.DisplayKind.VEGETABLES;
        }
    }

    @FeedRestoKind
    public static String getFeedRestoKindRaw(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_RESTO_KINDS, PREF_RESTO_KINDS_DEFAULT);
    }

    public static void setFeedRestoKind(Context context, @FeedRestoKind String kind) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit()
                .putString(PREF_RESTO_KINDS, kind)
                .apply();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_home_feed, rootKey);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DeleteViewModel.class);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), new EventObserver<Context>() {
            @Override
            protected void onUnhandled(Context data) {
                Toast.makeText(data, R.string.feed_pref_hidden_cleared, Toast.LENGTH_SHORT).show();
            }
        });

        requirePreference("pref_home_feed_clickable").setOnPreferenceClickListener(preference -> {
            viewModel.deleteAll();
            return true;
        });
        return v;
    }

    /**
     * The possible values for the {@link #PREF_RESTO_KINDS} preference. These are also defined in an XML array
     * resource.
     */
    @StringDef({FeedRestoKind.ALL, FeedRestoKind.MAIN, FeedRestoKind.SOUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FeedRestoKind {
        String ALL = "all";
        String SOUP = "soup";
        String MAIN = "main";
    }
}
