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

package be.ugent.zeus.hydra.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.PreferenceFragment;

/**
 * Show preferences related to the theme of the app.
 *
 * @author Niko Strijbol
 */
public class ThemeFragment extends PreferenceFragment {

    private static final String PREF_THEME_NIGHT_MODE = "pref_theme_night_mode";

    private String existing = null;
    private String updated = null;

    private static String defaultValue() {
        if (Build.VERSION.SDK_INT < 29) {
            return "battery";
        } else {
            return "system";
        }
    }

    @AppCompatDelegate.NightMode
    public static int getNightMode(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_THEME_NIGHT_MODE, defaultValue());
        switch (value) {
            case "battery":
                return AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
            case "dark":
                return AppCompatDelegate.MODE_NIGHT_NO;
            case "light":
                return AppCompatDelegate.MODE_NIGHT_YES;
            case "system":
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_theme, rootKey);

        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        existing = preferences.getString(PREF_THEME_NIGHT_MODE, defaultValue());

        ListPreference listPreference = requirePreference(PREF_THEME_NIGHT_MODE);
        if (Build.VERSION.SDK_INT < 29) {
            listPreference.setEntries(R.array.pref_dark_mode_old);
            listPreference.setEntryValues(R.array.pref_dark_mode_values_old);
            requirePreference("pref_theme_system").setVisible(false);

        } else {
            listPreference.setEntries(R.array.pref_dark_mode_new);
            listPreference.setEntryValues(R.array.pref_dark_mode_values_new);
            requirePreference("pref_theme_battery").setVisible(false);
        }
        listPreference.setDefaultValue(defaultValue());
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            Reporting.getTracker(getActivity()).log(new ThemeChanged((String) newValue));
            Toast.makeText(requireActivity().getApplicationContext(), R.string.pref_theme_night_mode_restart, Toast.LENGTH_LONG).show();
            updated = (String) newValue;
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(requireContext()).setCurrentScreen(
                requireActivity(), "Settings > Theme", getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (existing != null && !existing.equals(updated)) {
            AppCompatDelegate.setDefaultNightMode(getNightMode(requireContext()));
        }
    }

    private static class ThemeChanged implements Event {
        private final String newValue;

        private ThemeChanged(String newValue) {
            this.newValue = newValue;
        }

        @Nullable
        @Override
        public Bundle getParams() {
            Bundle bundle = new Bundle();
            bundle.putString("theme", newValue);
            return bundle;
        }

        @Nullable
        @Override
        public String getEventName() {
            return "be.ugent.zeus.hydra.theme";
        }
    }
}
