package za.co.whatsyourvibe.user.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.concurrent.Executor;

import za.co.whatsyourvibe.user.MainActivity;
import za.co.whatsyourvibe.user.R;
import za.co.whatsyourvibe.user.activities.EventDetailsActivity;
import za.co.whatsyourvibe.user.adapters.CustomInfoWindowAdapter;


public class NearMeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private static final String TAG = "NearMeFragment";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location mLastKnownLocation;

    private GoogleApiClient googleApiClient;

    private double currentLat, currentLng;

    private final static int REQUEST_CHECK_GPS = 123;

    private final static int REQUEST_ID_MULTIPLE_PERMISSION = 122;


    private BitmapDescriptor icon;

    public NearMeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_me, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        icon = BitmapDescriptorFactory.fromResource(R.drawable.primary_marker);

        if (getContext() != null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        setSupGoogleClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.near_me_map);
        mapFragment.getMapAsync(this);
    }

    private void setSupGoogleClient() {

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(),this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext()));

        mMap.setOnInfoWindowClickListener(this);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.map_primary_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }else{

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                }

                map.setMyLocationEnabled(true);

                getMyLocation();

                getEvents();


            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

    }


    private void getEvents() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e !=null) {

                            //error message here
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            return;
                        }

                        if (queryDocumentSnapshots !=null && !queryDocumentSnapshots.isEmpty()){

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                double lat = (double) doc.get("latitude");

                                double lng = (double) doc.get("longitude");

                                String title = (String) doc.get("name");

                                LatLng vibe = new LatLng(lat, lng);

                                mMap.addMarker(new MarkerOptions()
                                        .position(vibe)
                                        .icon(icon)
                                        .title(title));

                            }

                        }

                    }
                });

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

       Intent intent = new Intent(getActivity(), EventDetailsActivity.class);

       startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;

       // mMap.clear();

        if (mLastKnownLocation !=null) {

            currentLat = mLastKnownLocation.getLatitude();

            currentLng = mLastKnownLocation.getLongitude();

            LatLng latLng = new LatLng(currentLat,currentLng);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_hot_vibes);

            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.position(latLng);

            markerOptions.title("You Are Here");

            markerOptions.icon(icon);

            mMap.addMarker(markerOptions);

            getEvents();

            float zoomLevel = 16.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
    }

    private void getNearByEvents() {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        getMyLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(getContext(),
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {


                                        mLastKnownLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                        LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                        float zoomLevel = 13.0f; //This goes up to 21
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_GPS);


                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }


                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });

                }
            }
        }
    }

}
