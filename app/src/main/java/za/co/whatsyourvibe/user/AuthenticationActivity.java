package za.co.whatsyourvibe.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "AuthenticationActivity";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

       // setContentView(R.layout.activity_authentication);

        List<String> whitelistedCountries = new ArrayList<String>();
        whitelistedCountries.add("+27");

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        AuthUI.IdpConfig phoneConfigWithWhitelistedCountries = new AuthUI.IdpConfig.PhoneBuilder()
                .setWhitelistedCountries(whitelistedCountries)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.CustomFirebaseUITheme)
                        .setLogo(R.drawable.logo)
                        .setTosAndPrivacyPolicyUrls(
                                "http://whatsyourvibe.co.za/terms-and-conditions.pdf",
                                "http://whatsyourvibe.co.za/terms-and-conditions.pdf")
                        .build(),
                1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                createUserAccount();

            } else {

                if (response !=null && response.getError() !=null)  {

                    Toast.makeText(this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "onActivityResult: Error Occurred while signing up " + response.getError().getMessage() );

                }

            }
        }else{

            Toast.makeText(this, "An unexpected error has occurred. Please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    private void createUserAccount() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth !=null && auth.getCurrentUser() !=null) {

            String uid = auth.getUid();

            String email = auth.getCurrentUser().getEmail();

            String displayName = auth.getCurrentUser().getDisplayName();

            if (displayName == null) {

                displayName = "Guest";
            }

            Map<String,Object> user = new HashMap<>();

            user.put("emailAddress",email);

            user.put("displayName",displayName);


            db.collection("users")
                    .document(uid)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent i = new Intent(AuthenticationActivity.this, MainActivity.class);

                            startActivity(i);

                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e(TAG, "onFailure: " + e.getMessage() );

                            Toast.makeText(AuthenticationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }


    }
}
