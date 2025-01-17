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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DescriptionMultiSelectListViewHolder;
import be.ugent.zeus.hydra.common.utils.PreferencesUtils;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

import static be.ugent.zeus.hydra.feed.HomeFeedFragment.PREF_DISABLED_CARD_TYPES;

/**
 * Enables choosing the home feed card types.
 *
 * @author Niko Strijbol
 */
public class HomeFeedSelectFragment extends Fragment {

    private final Map<String, String> valueMapper = new HashMap<>();
    private FeedOptionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_feed_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new FeedOptionsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO improve how this is saved.
        String[] values = getResources().getStringArray(R.array.card_types_names);
        String[] descriptions = getResources().getStringArray(R.array.card_types_descriptions);
        String[] ints = getResources().getStringArray(R.array.card_types_nr);

        List<Tuple> itemTuples = new ArrayList<>();
        valueMapper.clear();
        for (int i = 0; i < values.length; i++) {
            valueMapper.put(values[i], ints[i]);
            itemTuples.add(new Tuple(values[i], descriptions[i]));
        }

        List<String> cardTypesList = Arrays.asList(ints);

        Set<Integer> unwanted = PreferencesUtils.getStringSet(getContext(), PREF_DISABLED_CARD_TYPES).stream()
                .map(cardTypesList::indexOf)
                .filter(integer -> integer != -1) // Non-existing ones are gone
                .collect(Collectors.toSet());

        adapter.submitData(itemTuples, unwanted, true);
    }

    @Override
    public void onPause() {
        super.onPause();

        //Save the settings.
        //We save which cards we DON'T want, so we need to inverse it.
        List<Pair<Tuple, Boolean>> values = adapter.getItemsAndState();
        Set<String> disabled = new HashSet<>();

        for (Pair<Tuple, Boolean> value : values) {
            if (!value.second) {
                disabled.add(valueMapper.get(value.first.getTitle()));
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        preferences.edit().putStringSet(PREF_DISABLED_CARD_TYPES, disabled).apply();
    }

    private static class Tuple {

        private final String title;
        private final String description;

        private Tuple(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class FeedOptionsAdapter extends MultiSelectAdapter<Tuple> {

        @NonNull
        @Override
        public DataViewHolder<Tuple> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DescriptionMultiSelectListViewHolder<>(
                    ViewUtils.inflate(parent, R.layout.item_checkbox_string_description),
                    this,
                    Tuple::getTitle,
                    Tuple::getDescription
            );
        }
    }
}
