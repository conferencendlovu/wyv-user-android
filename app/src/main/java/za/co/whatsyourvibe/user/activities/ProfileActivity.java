package za.co.whatsyourvibe.user.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    private TextView tvDisplayName;

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

    private void loadProfileDetails(String currentUserId) {

        db.collection("users")
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

                                Toast.makeText(ProfileActivity.this, "Profile Photo Not Available, please set", Toast.LENGTH_SHORT).show();
                            }
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

    private void initViews() {

        profileImage = findViewById(R.id.profile_ivPhoto);

        tvDisplayName = findViewById(R.id.profile_displayName);

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
