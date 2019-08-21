package za.co.whatsyourvibe.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import za.co.whatsyourvibe.user.R;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mWindow;

    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {

        mContext = context;

        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null );

    }

    private void renderInfoWindowText(Marker marker, View view) {

        String title =  marker.getTitle();

        TextView tvTitle = view.findViewById(R.id.custom_info_window_title);

        if (!title.equals("")) {

            tvTitle.setText(title);
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {

        renderInfoWindowText(marker,mWindow);

        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {

        renderInfoWindowText(marker,mWindow);

        return mWindow;
    }
}
