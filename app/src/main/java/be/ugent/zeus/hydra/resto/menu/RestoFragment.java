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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.ErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.common.utils.StringUtils;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.extrafood.ExtraFoodActivity;
import be.ugent.zeus.hydra.resto.history.HistoryActivity;
import be.ugent.zeus.hydra.resto.meta.RestoLocationActivity;
import be.ugent.zeus.hydra.resto.salad.SaladActivity;
import be.ugent.zeus.hydra.resto.sandwich.SandwichActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireBaseActivity;

/**
 * Displays the menu.
 *
 * @author Niko Strijbol
 */
public class RestoFragment extends Fragment implements
        AdapterView.OnItemSelectedListener,
        MainActivity.ArgumentsReceiver,
        NavigationBarView.OnItemSelectedListener,
        MainActivity.ScheduledRemovalListener {

    public static final String ARG_DATE = "start_date";
    private static final String TAG = "RestoFragment";
    private static final String ARG_POSITION = "arg_pos";

    private static final String URL = "https://www.ugent.be/student/nl/meer-dan-studeren/resto";
    private MenuPagerAdapter pageAdapter;
    private ViewPager2 viewPager;
    private MenuViewModel menuViewModel;
    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigation;

    /**
     * The saved position of the viewpager. Used to manually restore the position, since it is possible that the state
     * is restored before the data is restored. -1 indicates we don't have to restore.
     */
    private int mustBeRestored = -1;

    /**
     * The start date for which resto to show.
     */
    @Nullable
    private LocalDate startDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resto, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // If the data has been set, we don't need to restore anything, since Android does it for us.
        if (savedInstanceState != null && !pageAdapter.hasData()) {
            mustBeRestored = savedInstanceState.getInt(ARG_POSITION, -1);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_POSITION, viewPager.getCurrentItem());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "receiveResto: on view created");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pageAdapter = new MenuPagerAdapter(this);

        // Set up the ViewPager with the sections adapter.
        viewPager = view.findViewById(R.id.resto_tabs_content);
        viewPager.setAdapter(pageAdapter);

        // Make the tab layout from the main activity visible.
        tabLayout = requireActivity().findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setVisibility(View.VISIBLE);

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.resto_tab_title_legend);
            } else {
                String title = DateUtils.getFriendlyDate(requireContext(), Objects.requireNonNull(pageAdapter.getTabDate(position)));
                String capitalized = StringUtils.capitaliseFirst(title);
                tab.setText(capitalized);
            }
        });
        mediator.attach();

        bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigation.setVisibility(View.VISIBLE);
        bottomNavigation.setOnItemSelectedListener(this);

        Bundle extras = getArguments();
        //Get the default start date
        if (extras != null && extras.containsKey(ARG_DATE)) {
            startDate = (LocalDate) extras.getSerializable(ARG_DATE);
        }

        final ViewModelProvider provider = new ViewModelProvider(this);

        menuViewModel = provider.get(MenuViewModel.class);
        menuViewModel.getData().observe(getViewLifecycleOwner(), ErrorObserver.with(this::onError));
        menuViewModel.getData().observe(getViewLifecycleOwner(), new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        menuViewModel.getData().observe(getViewLifecycleOwner(), SuccessObserver.with(this::receiveData));
    }

    private void receiveData(@NonNull List<RestoMenu> data) {
        Log.d(TAG, "receiveData: received adapter info, start date is " + startDate);
        pageAdapter.setData(data);
        // We need to manually restore this, see mustBeRestored.
        // If the data has changed and we can't select the old date, fall back to the default.
        if (mustBeRestored != -1 && mustBeRestored < data.size()) {
            viewPager.setCurrentItem(mustBeRestored);
            mustBeRestored = -1;
        } else {
            // In the default case we select the initial date if present.
            if (startDate != null) {
                for (int i = 0; i < data.size(); i++) {
                    RestoMenu menu = data.get(i);
                    //Set the tab to this day!
                    if (menu.getDate().isEqual(startDate)) {
                        Log.d(TAG, "receiveData: setting item to " + (i + 1));
                        TabLayout.Tab tab = tabLayout.getTabAt(i + 1);
                        if (tab != null) {
                            tab.select();
                            Log.d(TAG, "receiveData: NOT NULL");
                        } else {
                            viewPager.setCurrentItem(i + 1);
                        }
                        break;
                    }
                }
            } else if (pageAdapter.hasData()) {
                TabLayout.Tab tab = tabLayout.getTabAt(1);
                if (tab != null) {
                    tab.select();
                    Log.d(TAG, "receiveData: NOT NULL");
                } else {
                    viewPager.setCurrentItem(1);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_resto, menu);
        requireBaseActivity(this).tintToolbarIcons(menu, R.id.action_history);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            Toast toast = Toast.makeText(getContext(), R.string.resto_extra_refresh_started, Toast.LENGTH_SHORT);
            toast.show();
            menuViewModel.onRefresh();
            return true;
        } else if (itemId == R.id.resto_show_website) {
            NetworkUtils.maybeLaunchBrowser(getContext(), URL);
            return true;
        } else if (itemId == R.id.action_history) {
            startActivity(new Intent(getContext(), HistoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(@NonNull AdapterView<?> parent, View view, int position, long id) {
        //The start should be the day we have currently selected.
        if (pageAdapter.getItemCount() > viewPager.getCurrentItem()) {
            startDate = pageAdapter.getTabDate(viewPager.getCurrentItem());
        }
        menuViewModel.onRefresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    private void onError(Throwable throwable) {
        pageAdapter.setData(Collections.emptyList());
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> menuViewModel.onRefresh())
                .show();
    }

    @Override
    public void fillArguments(@NonNull Intent activityIntent, Bundle bundle) {
        if (activityIntent.hasExtra(ARG_DATE)) {
            bundle.putSerializable(ARG_DATE, activityIntent.getSerializableExtra(ARG_DATE));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.resto_bottom_sandwich) {
            startActivity(new Intent(getContext(), SandwichActivity.class));
            return false;
        } else if (itemId == R.id.resto_bottom_salad) {
            startActivity(new Intent(getContext(), SaladActivity.class));
            return false;
        } else if (itemId == R.id.resto_bottom_locations) {
            startActivity(new Intent(getContext(), RestoLocationActivity.class));
            return false;
        } else if (itemId == R.id.resto_bottom_extra) {
            startActivity(new Intent(getContext(), ExtraFoodActivity.class));
            return false;
        }
        return false;
    }

    @Override
    public void onRemovalScheduled() {
        hideExternalViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideExternalViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabLayout.setVisibility(View.GONE);
    }

    private void hideExternalViews() {
        bottomNavigation.setVisibility(View.GONE);
        bottomNavigation.setOnNavigationItemSelectedListener(null);
    }
}
