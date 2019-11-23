package za.co.whatsyourvibe.user.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import za.co.whatsyourvibe.user.models.Event;

import static android.app.Activity.RESULT_OK;


public class NearMeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private static final String TAG = "NearMeFragment";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location mLastKnownLocation;

    private LocationCallback locationCallback;


    private double currentLat, currentLng;

    private final static int REQUEST_CHECK_GPS = 123;


    private BitmapDescriptor icon;

    public NearMeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near_me, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {

            if (resultCode == RESULT_OK) {

                getDeviceLocation();
            }

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        icon = BitmapDescriptorFactory.fromResource(R.drawable.primary_marker);

        if (getContext() != null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.near_me_map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful()) {

                    mLastKnownLocation = task.getResult();

                    if (mLastKnownLocation !=null) {

                        mMap.moveCamera(CameraUpdateFactory
                                                .newLatLngZoom(
                                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                                mLastKnownLocation.getLongitude()),16.0f));

                    }else {

                        // if null, request for the updated location
                        final LocationRequest locationRequest = LocationRequest.create();

                        locationRequest.setInterval(10000);

                        locationRequest.setFastestInterval(5000);

                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {

                                super.onLocationResult(locationResult);

                                if (locationResult == null) {

                                    return;

                                }

                                mLastKnownLocation = locationResult.getLastLocation();

                                mMap.moveCamera(CameraUpdateFactory
                                                        .newLatLngZoom(
                                                                new LatLng(mLastKnownLocation.getLatitude(),
                                                                        mLastKnownLocation.getLongitude()),16.0f));

                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                locationCallback, null);
                    }


                }else{

                    Toast.makeText(getContext(), "Unable to get last location",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });

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

            // getEvents();



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

                checkIfGpsIsEnable();

                getEvents();


            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

    }

    private void checkIfGpsIsEnable() {
        // chexking if GPS is enabled
        LocationRequest locationRequest = LocationRequest.create();

        locationRequest.setInterval(10000);

        locationRequest.setFastestInterval(5000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                                          .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                getDeviceLocation();

            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if ( e instanceof ResolvableApiException) {

                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;

                    try {
                        resolvableApiException.startResolutionForResult(getActivity(), 1234);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });
    }


    private void getEvents() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("vibes")
                .whereEqualTo("status","Active")
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

                                Event vibe = doc.toObject(Event.class);

                                //double lat = (double) doc.get("lat");

                                //double lng = (double) doc.get("long");


                                String title = vibe.getTitle();

                                String eventId = doc.getId();

                                LatLng vibeCoordinate = new LatLng(vibe.getLat(), vibe.getLng());

                                mMap.addMarker(new MarkerOptions()
                                        .position(vibeCoordinate)
                                        .icon(icon)
                                        .snippet(eventId)
                                        .title(title));

                            }

                        }

                    }
                });

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

       Intent intent = new Intent(getActivity(), EventDetailsActivity.class);

       intent.putExtra("EVENT_ID", marker.getSnippet());

       startActivity(intent);
    }

}
