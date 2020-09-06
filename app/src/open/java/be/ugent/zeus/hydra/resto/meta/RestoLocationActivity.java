package be.ugent.zeus.hydra.resto.meta;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityRestoLocationBinding;
import com.google.android.material.snackbar.Snackbar;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class RestoLocationActivity extends BaseActivity<ActivityRestoLocationBinding> {

    private static final String TAG = "RestoLocationActivity";

    private static final GeoPoint DEFAULT_LOCATION = new GeoPoint(51.05, 3.72); //Gent
    private static final float DEFAULT_ZOOM = 15;

    private static final int MY_LOCATION_REQUEST_CODE = 1;

    private RestoMeta meta;
    private MetaViewModel viewModel;
    private MyLocationNewOverlay myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(ActivityRestoLocationBinding::inflate);

        binding.map.setTileSource(TileSourceFactory.MAPNIK);
        binding.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        binding.map.setMultiTouchControls(true);

        IMapController mapController = binding.map.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        mapController.setCenter(DEFAULT_LOCATION);

        viewModel = new ViewModelProvider(this).get(MetaViewModel.class);
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(binding.progressBar));
        viewModel.getData().observe(this, SuccessObserver.with(this::receiveData));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
    }

    private void receiveData(@NonNull RestoMeta data) {
        meta = data;
        addData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resto_location, menu);
        tintToolbarIcons(menu, R.id.resto_refresh, R.id.resto_center);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resto_refresh:
                viewModel.onRefresh();
                return true;
            case R.id.resto_center:
                if (this.myLocation != null) {
                    IMapController mapController = binding.map.getController();
                    mapController.setCenter(myLocation.getMyLocation());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addData() {

        for (Resto location : meta.locations) {
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            Marker m = new Marker(binding.map);
            m.setPosition(point);
            m.setTitle(location.getName());
            m.setSubDescription(location.getAddress());
            m.setDraggable(false);
            binding.map.getOverlayManager().add(m);
        }
        binding.progressBar.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            }
        }
    }

    private void enableLocation() {
        if (myLocation != null) {
            myLocation.disableMyLocation();
            binding.map.getOverlayManager().remove(myLocation);
        }
        myLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(this), binding.map);
        myLocation.enableMyLocation();
        binding.map.getOverlayManager().add(myLocation);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.map.onPause();
    }
}
