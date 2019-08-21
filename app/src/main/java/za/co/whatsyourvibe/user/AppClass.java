package za.co.whatsyourvibe.user;

import android.app.Application;
import android.content.Context;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AppClass extends Application {

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
        // MultiDex.install(this);
    }

    @Override
    public void onCreate() {

        super.onCreate();

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }
}
