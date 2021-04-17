package org.me.gcu.British_GSurvey_EQuake;
/* Mobile Platform Development Assignment Session 2020/2021 MHI322959/MHI325105
    Student Name : Kirubel Afework
*   Student No: S1732434
*/

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.ClusterManager;

import org.me.gcu.British_GSurvey_EQuake.controller.Filter_eq;
import org.me.gcu.British_GSurvey_EQuake.controller.Task;
import org.me.gcu.British_GSurvey_EQuake.model.Adapter;
import org.me.gcu.British_GSurvey_EQuake.model.CustomClusterRenderer;
import org.me.gcu.British_GSurvey_EQuake.model.ItemClass;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements OnClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, AdapterView.OnItemSelectedListener {

    public static LinkedList<ItemClass> alist;
    public ClusterManager<ItemClass> clusterManager;

    private Spinner spinner;
    private static final String[] paths = {"All Earthquakes", "1.0+ Earthquakes", "2.5+ Earthquakes", "4.5+ Earthquakes", "Significant Earthquakes"};

    public TextView rawDataDisplay;
    private TextView dateDisplay;
    private TextView lastUpdate;
    private int counter;
    private long timeleftinmillseconds = 600000;


    private Button dateRange;
    private String result = "";
    private String url1 = "";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private Button s1Button;
    private Button s2Button;
    private ViewSwitcher viewSwitcher;
    Thread Thread1;

    RecyclerView recyclerView;
    Adapter adapter;
    Filter_eq filter_eq;

    private static final int REQUEST_LOCATION_PERMISSION = 1;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //  mapsActivity.onCreate(savedInstanceState);
        //  setContentView(R.layout.content_main);

        // Set up the raw links to the graphical components
        rawDataDisplay = findViewById(R.id.rawDataDisplay);
        //  dateDisplay = findViewById(R.id.dateDisplay);
        lastUpdate = findViewById(R.id.lastUpdate);

//        startButton = (Button) findViewById(R.id.startButton);
//        startButton.setOnClickListener(this);

        viewSwitcher = findViewById(R.id.vwSwitch);
        if (viewSwitcher == null) {
            Toast.makeText(getApplicationContext(), "Null ViewSwicther", Toast.LENGTH_LONG);

        }

        s1Button = findViewById(R.id.screen1Button);
        s2Button = findViewById(R.id.screen2Button);
        s1Button.setOnClickListener(this);
        s2Button.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mMap);
        mapFragment.getMapAsync(this);

        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        startProgress();
        refresh();
        timer();

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.bringToFront();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.navigation_home):
                        viewSwitcher.showNext();
                        // item.setEnabled(false);
                        item.getIcon().setAlpha(130);

                        break;
                    case R.id.more_details:
                        viewSwitcher.showPrevious();
                        //  item.setEnabled(false);


                        if (R.id.more_details == item.getItemId()) {
                            //       onNavigationItemSelectedR.id.navigation_home

                        }


                        break;
                    case R.id.filter:
//                        public void onClick(View v){
                        Intent i = new Intent(getApplicationContext(), Filter_eq.class);

                        startActivity(i);

                        break;
                }
                return false;
            }
        });
    }

    public void onClick(View aview) {

        mMap.clear();
        if (aview == s1Button) {
            viewSwitcher.showNext();
        } else if (aview == s2Button) {
            viewSwitcher.showPrevious();
        }

    }


    private void refresh() {
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mMap.clear();
                timeleftinmillseconds = 600000;

                //Do your refreshing
                startProgress();
                timer();
            }
        };

        // update every 10 minutes
        handler.postDelayed(runnable, 600000);
    }


    private void timer() {
        new CountDownTimer(600000, 1000) {
            @Override
            public void onTick(long l) {
                timeleftinmillseconds = l;
                updateTimer();
            }

            public void onFinish() {
                //  timer();
                //  lastUpdate.setText("Finsh");
                timer();
                refresh();
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) timeleftinmillseconds / 60000;
        int seconds = (int) timeleftinmillseconds % 60000 / 1000;
        String timeleft;

        timeleft = "" + minutes;
        timeleft += ":";
        if (seconds < 10) timeleft += "0";

        timeleft += seconds;
        lastUpdate.setText("Information update in " + timeleft);

    }

    public void startProgress() {
        // Run network access on a separate thread;
        Thread1 = new Thread(new Task(urlSource, this));
        Thread1.start();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }


    public void show() {

        for (int i = 0; i < alist.size(); i++) {

            double lat = alist.get(i).getLat();
            double lng = alist.get(i).getLng();
            String Title = alist.get(i).getLocation();

            String Magnitude = String.valueOf(alist.get(i).getMagnitude() + alist.get(i).getLng());
            ItemClass offsetItem = new ItemClass(lat, lng, "" + Title, "Magnitude:" + Magnitude, alist.get(i).getMagnitude());
            clusterManager.addItem(offsetItem);

        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, alist);
        recyclerView.setAdapter(adapter);
        clusterManager.cluster();
    }

    public void setUpClusterer() {

        clusterManager = new ClusterManager<ItemClass>(this, mMap);

        CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, clusterManager);
        clusterManager.setRenderer(renderer);

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
        show();
        // Position the map.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.8642, -5.2518), 4));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Glasgow and move the camera
        LatLng glasgow = new LatLng(55.8642, 4.2518);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 1));
        mMap.setTrafficEnabled(true);
        mMap.getMinZoomLevel();
        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.text_location_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_LOCATION_PERMISSION);
                        }
                    })
                    .create()
                    .show();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        // Enable location tracking.
        //  setMapLongClick(mMap); // Set a long click listener for the map;

        mMap.setOnInfoWindowClickListener(this);

    }


    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.maptypeHYBRID:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                }
            case R.id.maptypeNONE:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    return true;
                }
            case R.id.maptypeNORMAL:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                }
            case R.id.maptypeSATELLITE:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                }
            case R.id.maptypeTERRAIN:
                if (mMap != null) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                break;
            case 3:
                // Whatever you want to happen when the forth item gets selected
                break;
            case 4:
                // Whatever you want to happen when the fifth item gets selected
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public boolean onMarkerClick(Marker marker) {
        //    Log.d(MainActivity.TAG, "onMarkerClick() called with: marker = [" + marker.getTitle() + "]");
        return false;
    }

}