package za.co.whatsyourvibe.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import za.co.whatsyourvibe.user.activities.ProfileActivity;
import za.co.whatsyourvibe.user.adapters.EventsSectionsPageAdapter;
import za.co.whatsyourvibe.user.fragments.CategoriesFragment;
import za.co.whatsyourvibe.user.fragments.NearMeFragment;
import za.co.whatsyourvibe.user.fragments.TrendingFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    private EventsSectionsPageAdapter eventsSectionsPageAdapter;

    public static boolean PERMISSION_GRANTED = false;

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        checkMapServices();

    }

    @Override
    protected void onResume() {
        super.onResume();

       // checkMapServices();
    }

    private boolean checkMapServices(){

        if(isServicesOK()){

            if(isMapsEnabled()){

                return true;

            }

        }

        return false;
    }

    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);

                    }

                });

        final AlertDialog alert = builder.create();

        alert.show();
    }

    public boolean isMapsEnabled(){

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

            buildAlertMessageNoGps();

            return false;

        }

        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            PERMISSION_GRANTED = true;

            // do what you want to do here

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){

            //everything is fine and the user can make map requests

            Log.d(TAG, "isServicesOK: Google Play Services is working");

            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            //an error occured but we can resolve it

            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);

            dialog.show();

        }else{

            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();

        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        PERMISSION_GRANTED = false;

        switch (requestCode) {

            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    PERMISSION_GRANTED = true;

                }

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: called.");

        switch (requestCode) {

            case PERMISSIONS_REQUEST_ENABLE_GPS: {

                if(PERMISSION_GRANTED){

                    // do something here

                }

                else{

                    getLocationPermission();

                }
            }
        }

    }

    private void initViews() {

        eventsSectionsPageAdapter = new EventsSectionsPageAdapter(getSupportFragmentManager());

        //setup view pager
        ViewPager viewPager = findViewById(R.id.main_viewPager);

        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.main_tabLayout);

        Toolbar toolbar = findViewById(R.id.main_toolbar);

        TextView mTitle = toolbar.findViewById(R.id.toolbar_title_main);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() !=null) {

            mTitle.setText("Whats Your Vibe");

            getSupportActionBar().setDisplayShowTitleEnabled(false);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_profile) {

            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);

            startActivity(intent);

        }

        if (id == R.id.menu_sign_out) {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.signOut();

            Toast.makeText(this, "Thank you for using Whats Your Vibe", Toast.LENGTH_SHORT).show();

            getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {

        EventsSectionsPageAdapter  adapter = new EventsSectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new CategoriesFragment(), "Categories");

        adapter.addFragment(new NearMeFragment(), "Near Me");

        adapter.addFragment(new TrendingFragment(), "Trending");

        viewPager.setAdapter(adapter);
    }
}
