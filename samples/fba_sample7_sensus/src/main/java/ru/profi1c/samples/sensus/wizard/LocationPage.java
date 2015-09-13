package ru.profi1c.samples.sensus.wizard;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.profi1c.engine.map.location.ILastLocationFinder;
import ru.profi1c.engine.map.location.PlatformSpecificImplementationFactory;
import ru.profi1c.engine.widget.FieldEditText;
import ru.profi1c.samples.sensus.Const;
import ru.profi1c.samples.sensus.Dbg;
import ru.profi1c.samples.sensus.R;
import ru.profi1c.samples.sensus.db.CatalogSalesPoints;

public class LocationPage extends BaseSalesPointPage {

    private static final boolean DEFAULT_ENABLE_LOCATION = true;
    private static final int MAP_POINT_ZOOM_LEVEL = 17;

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private Geocoder mGeocoder;

    private View mCoordinatorLayout;
    private GoogleMap mMap;
    private Location mLastLocation;

    private TextInputLayout mTilAddress;
    private FieldEditText mEtAddress;
    private TextView mTvLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_page_location, container, false);
        initControls(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inflateData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_page_location, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_refresh_location);
        if (item != null && mMap != null) {
            item.setChecked(mMap.isMyLocationEnabled());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_refresh_location) {
            item.setChecked(!item.isChecked());
            switchMapCurrentLocation(item.isChecked());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initControls(View root) {
        mCoordinatorLayout = root.findViewById(R.id.clPageLocation);
        mTilAddress = (TextInputLayout) root.findViewById(R.id.tilAddress);
        mEtAddress = (FieldEditText) root.findViewById(R.id.etAddress);
        mEtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mTilAddress.setErrorEnabled(false);
            }
        });
        mTvLocation = (TextView) root.findViewById(R.id.tvLocation);
        mTvLocation.setVisibility(View.VISIBLE);

        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {

            /* Получить последнее известное местоположение по заданным критериям
            (считаем его валидным как приблизительные координаты торговой  точки)
             */
            ILastLocationFinder locationFinder =
                    PlatformSpecificImplementationFactory.getLastLocationFinder(getActivity());
            Location location = locationFinder
                    .getLastBestLocation(Const.LOCATION_MIN_DISTANCE, Const.LOCATION_MIN_TIME);
            if (location != null) {
                onLocationChanged(location, false);
            }

            mMap.setMyLocationEnabled(DEFAULT_ENABLE_LOCATION);
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    onLocationChanged(location, true);
                }
            });

        } else {
            Snackbar.make(mCoordinatorLayout, R.string.msg_not_support_google_maps,
                          Snackbar.LENGTH_SHORT).show();
        }
    }

    private void switchMapCurrentLocation(boolean enabled) {
        if (mMap != null) {
            mMap.setMyLocationEnabled(enabled);
        }
    }

    private void inflateData() {
        //Связать поле 'Адрес' торговой точки с элементам управления
        mEtAddress.build(getSalesPoint(), CatalogSalesPoints.FIELD_NAME_ADDRESS, null);
        updateSalesPointLocation();
    }

    private void updateSalesPointLocation() {
        CatalogSalesPoints point = getSalesPoint();
        if (point != null && mLastLocation != null) {
            point.lat = mLastLocation.getLatitude();
            point.lng = mLastLocation.getLongitude();
        }
    }

    private void onLocationChanged(Location location, boolean updateAddress) {
        mLastLocation = location;
        updateSalesPointLocation();

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mTvLocation
                .setText(String.format(getString(R.string.hint_location_frm), latitude, longitude));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
                                                             MAP_POINT_ZOOM_LEVEL));

        if (updateAddress) {
            getGeocoderAddress();
        }
    }

    private void getGeocoderAddress() {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addressList = mGeocoder
                            .getFromLocation(mLastLocation.getLatitude(),
                                             mLastLocation.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(", ");
                        }

                        String postalCode = address.getPostalCode();
                        if (TextUtils.isEmpty(postalCode)) {
                            sb.setLength(sb.length() - 2);
                        } else {
                            sb.append(postalCode);
                        }
                        final String strAddress = sb.toString();

                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mEtAddress.setText(strAddress);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    Dbg.printStackTrace(e);
                }
            }
        });
    }

    @Override
    void onPageSelected() {

    }

    @Override
    boolean onSaveSalesPoint(CatalogSalesPoints salesPoint) {
        if (TextUtils.isEmpty(salesPoint.address)) {
            mTilAddress.setError(getString(R.string.err_address_is_empty));
            return false;
        }
        return true;
    }
}
