package org.me.gcu.British_GSurvey_EQuake.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.me.gcu.British_GSurvey_EQuake.ItemClass;
import org.me.gcu.British_GSurvey_EQuake.R;

public class CustomClusterRenderer extends DefaultClusterRenderer<ItemClass> {

    private final IconGenerator mClusterIconGenerator;
    private final Context mContext;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<ItemClass> clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext);
    }

    @Override
    protected void onBeforeClusterItemRendered(ItemClass item, MarkerOptions markerOptions) {

        if (item.getMagnitude() < 1) {

            markerOptions.title("").icon(bitmapDescriptorFromVector(CustomClusterRenderer.this, R.drawable.ic_g_eq)).snippet(item.getSnippet());

        } else if (item.getMagnitude() >= 1 && item.getMagnitude() <= 2) {

            markerOptions.title("").icon(bitmapDescriptorFromVector(CustomClusterRenderer.this, R.drawable.ic_y_eq)).snippet(item.getSnippet());

        } else if (item.getMagnitude() >= 2 && item.getMagnitude() <= 2.9999) {

            markerOptions.title("").icon(bitmapDescriptorFromVector(CustomClusterRenderer.this, R.drawable.ic_o_eq)).snippet(item.getSnippet());

        } else if (item.getMagnitude() >= 3 && item.getMagnitude() <= 4.9999) {

            markerOptions.title("").icon(bitmapDescriptorFromVector(CustomClusterRenderer.this, R.drawable.ic_r_eq)).snippet(item.getSnippet());

        } else if (item.getMagnitude() >= 5 && item.getMagnitude() < 6.9999) {

            markerOptions.title("").icon(bitmapDescriptorFromVector(CustomClusterRenderer.this, R.drawable.ic_rb_eq)).snippet(item.getSnippet());

        } else if (item.getMagnitude() > 7) {

            markerOptions.title("").icon(bitmapDescriptorFromVector(CustomClusterRenderer.this, R.drawable.ic_rb_eq)).snippet(item.getSnippet());

        }

        super.onBeforeClusterItemRendered(item, markerOptions);

    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ItemClass> cluster, MarkerOptions markerOptions) {

        if (cluster.getSize() > 6) {
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_w_eq));
        } else {
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_bb_eq));
        }

        mClusterIconGenerator.setColor(Color.BLUE);

        mClusterIconGenerator.setTextAppearance(R.style.Theme_British_GSurvey_EQuake_WhiteTextAppearance);

        String clusterTitle = String.valueOf(cluster.getSize());
        Bitmap icon = mClusterIconGenerator.makeIcon(clusterTitle);

        mClusterIconGenerator.setTextAppearance(R.style.Theme_British_GSurvey_EQuake_WhiteTextAppearance);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

    }

    private BitmapDescriptor bitmapDescriptorFromVector(CustomClusterRenderer context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(mContext, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}