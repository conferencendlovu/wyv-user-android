package za.co.whatsyourvibe.user.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.infideap.stylishwidget.view.AMeter;
import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import za.co.whatsyourvibe.user.R;

public class ProfileActivity extends AppCompatActivity {

    private static final int RC_IMAGE_PICKER = 200;

    private static final int RC_PERMISSION = 200;

    private CircleImageView profileImage;

    private String imageUrl;

    private StorageReference mStorageReference;

    private String currentUserId;

    private Uri mImageUri;

    private FirebaseFirestore db;

    private TextView tvDisplayName, tvVibesRated;

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        mStorageReference = FirebaseStorage.getInstance().getReference("profile_images");

        FirebaseAuth auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        if (auth !=null) {

            currentUserId = auth.getUid();

            loadProfileDetails(currentUserId);

            getUserPoints(currentUserId);
        }

        Toolbar toolbar = findViewById(R.id.profile_toolbar);

        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() !=null) {

            mTitle.setText("Profile");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }

        initViews();

    }

    private void getUserPoints(String currentUserId) {

        db.collection("events_raters")
                .document(currentUserId)
                .collection("my_ratings")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            tvVibesRated.setText(queryDocumentSnapshots.size()+"");

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void loadProfileDetails(final String currentUserId) {

        db.collection("vibers")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            if (documentSnapshot.get("photoUrl") !=null) {

                                Glide.with(getApplicationContext())
                                        .load(documentSnapshot.get("photoUrl"))
                                        .placeholder(R.drawable.default_profile_image)
                                        // .fitCenter()
                                        .into(profileImage);

                            }else{

                                showUpdateProfileDialog(currentUserId);

                            }
                        }else {

                            showUpdateProfileDialog(currentUserId);
                        }

                        if (documentSnapshot.get("displayName") !=null) {

                            tvDisplayName.setText(documentSnapshot.get("displayName").toString());
                        }else{
                            Toast.makeText(ProfileActivity.this, "Display Not Set", Toast.LENGTH_SHORT).show();
                            tvDisplayName.setText("Guest");
                        }

                    }
                });

    }

    private void showUpdateProfileDialog(String currentUserId) {

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_profile, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText displayName = dialogView.findViewById(R.id.dialog_update_profile_etDisplayName);

        final EditText emailAddress =
                dialogView.findViewById(R.id.dialog_update_profile_etEmailAddress);

        final EditText age = dialogView.findViewById(R.id.dialog_update_profile_etAge);

        final Spinner gender = dialogView.findViewById(R.id.dialog_update_profile_spnGender);

        final EditText city = dialogView.findViewById(R.id.dialog_update_profile_etCity);

        final Spinner province = dialogView.findViewById(R.id.dialog_update_profile_spnProvince);

        Button updateProfile = dialogView.findViewById(R.id.dialog_update_profile_btnUpdateProfile);


        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(displayName.getText())) {

                    displayName.setError("Your name is required!");

                    displayName.requestFocus();

                    return;
                }

                if (TextUtils.isEmpty(emailAddress.getText())) {

                    emailAddress.setError("Email address is required!");

                    emailAddress.requestFocus();

                    return;
                }

                if (TextUtils.isEmpty(age.getText())) {

                    age.setError("Age is required!");

                    age.requestFocus();

                    return;
                }


                if (TextUtils.isEmpty(city.getText())) {

                    city.setError("City is required!");

                    city.requestFocus();

                    return;
                }

                if (gender.getSelectedItemPosition() == 0) {

                    Toast.makeText(ProfileActivity.this, "Please select your gender",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                if (province.getSelectedItemPosition() == 0) {

                    Toast.makeText(ProfileActivity.this, "Please select your province",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                

            }
        });

        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.show();

    }

    private void initViews() {

        profileImage = findViewById(R.id.profile_ivPhoto);

        tvDisplayName = findViewById(R.id.profile_displayName);

        tvVibesRated = findViewById(R.id.profile_tvVibesRated);

        setListeners();
    }

    private void setListeners() {

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermission();
            }
        });

    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {

                // permission not granted. ask for it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                // show pop up
                requestPermissions(permissions,RC_PERMISSION);

            }else {

                // permission already granted
                pickImageFromGallery();
            }

        }else{
            // device less then marshmallow

            pickImageFromGallery();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case  RC_PERMISSION :

                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    pickImageFromGallery();

                }else{

                    // permission denied
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void pickImageFromGallery() {

        // intent to pick image
        Intent intent  =   new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        startActivityForResult(intent, RC_IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RC_IMAGE_PICKER) {

            profileImage.setImageURI(data.getData());

            mImageUri = data.getData();

            uploadImage();

        }
    }

    private void uploadImage() {

        final StorageReference fileReference =
                mStorageReference.child(currentUserId);

        UploadTask uploadTask = fileReference.putFile(mImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        imageUrl = uri.toString();

                        Toast.makeText(ProfileActivity.this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show();

                        updateUserProfile();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }

    private void updateUserProfile() {

        db.collection("users")
                .document(currentUserId)
                .update("photoUrl",imageUrl);
    }
}
