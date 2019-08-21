package za.co.whatsyourvibe.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView logo = findViewById(R.id.splash_logo);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        Glide.with(this)
                .load(R.drawable.v)
                .placeholder(R.drawable.v)
                .into(logo);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (auth.getCurrentUser() !=null) {

                    Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);

                    startActivity(intent);
                }else{

                    Intent intent = new Intent(SplashScreenActivity.this,AuthenticationActivity.class);

                    startActivity(intent);
                }

                finish();

            }
        }, 3000);
    }
}
