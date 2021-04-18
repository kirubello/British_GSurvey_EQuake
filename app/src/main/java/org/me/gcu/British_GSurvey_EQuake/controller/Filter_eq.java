package org.me.gcu.British_GSurvey_EQuake.controller;

/* Mobile Platform Development Assignment Session 2020/2021 MHI322959/MHI325105
    Student Name : Kirubel Afework
*   Student No: S1732434
*/

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.maps.android.clustering.ClusterManager;

import org.me.gcu.British_GSurvey_EQuake.MainActivity;
import org.me.gcu.British_GSurvey_EQuake.R;
import org.me.gcu.British_GSurvey_EQuake.model.CustomClusterRenderer;
import org.me.gcu.British_GSurvey_EQuake.model.ItemClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;


public class Filter_eq extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, View.OnClickListener {

    public ClusterManager<ItemClass> clusterManager;

    private GoogleMap mMap;
    private TextView textTitle;
    private Button dateRange;
    private Button northerly;
    private Button southerly;
    private Button easterly;
    private Button westerly;
    private TextView dateDisplay;
    private LinkedList<ItemClass> alist;
    private ViewSwitcher viewSwitcher;

    LinkedList<ItemClass> f_Alist;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_eq);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.alist = new LinkedList<>(MainActivity.alist);

        dateRange = findViewById(R.id.date_Rangepicker);
        dateDisplay = findViewById(R.id.dateDisplay);

        northerly = findViewById(R.id.Northerly);
        northerly.setOnClickListener(this);
        northerly.getBackground().setAlpha(200);
        northerly.setBackgroundColor(Color.WHITE);
        northerly.setTextColor(Color.BLACK);

        southerly = findViewById(R.id.Southerly);
        southerly.setOnClickListener(this);
        southerly.getBackground().setAlpha(200);
        southerly.setBackgroundColor(Color.WHITE);
        southerly.setTextColor(Color.BLACK);

        easterly = findViewById(R.id.Easterly);
        easterly.setOnClickListener(this);
        easterly.getBackground().setAlpha(200);
        easterly.setBackgroundColor(Color.WHITE);
        easterly.setTextColor(Color.BLACK);

        westerly = findViewById(R.id.Westerly);
        westerly.setOnClickListener(this);
        westerly.getBackground().setAlpha(200);
        westerly.setBackgroundColor(Color.WHITE);
        westerly.setTextColor(Color.BLACK);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mMap);
        mapFragment.getMapAsync(this);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        long today = MaterialDatePicker.todayInUtcMilliseconds();

        calendar.setTimeInMillis(today);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        long january = calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        long december = calendar.getTimeInMillis();

        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setStart(january);
        constraintBuilder.setEnd(today);
        constraintBuilder.setValidator(DateValidatorPointBackward.now());

        MaterialDatePicker.Builder<Pair<Long, Long>> rangeDate = MaterialDatePicker.Builder.dateRangePicker();
        rangeDate.setTitleText("Selected Date Range");
        rangeDate.setCalendarConstraints(constraintBuilder.build());

        final MaterialDatePicker materialDatePicker = rangeDate.build();

        dateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "date_picker");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                //                Get the selected DATE RANGE
                Pair selectedDates = (Pair) materialDatePicker.getSelection();
//              then obtain the startDate & endDate from the range
                final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selectedDates.first), new Date((Long) selectedDates.second));
//              assigned variables
                Date startDate = rangeDate.first;
                Date endDate = rangeDate.second;
//              Format the dates in ur desired display mode
                SimpleDateFormat simpleFormat = new SimpleDateFormat("dd MMM yyyy");
//              Display it by setText
                dateDisplay.setText("SELECTED DATE : " + simpleFormat.format(startDate) + " --- " + simpleFormat.format(endDate));

                f_Alist = new LinkedList<>();

                if (f_Alist != null || f_Alist.size() != 0) {
                    f_Alist.clear();
                    mMap.clear();
                    clusterManager.clearItems();
                }
                if (f_Alist == null || f_Alist.size() == 0) {

                    for (ItemClass itemClass : alist) {
                        if (itemClass.getPubDate().after(startDate) && itemClass.getPubDate().before(endDate)) {

                            f_Alist.add(itemClass);
                        }
                    }

                    show(f_Alist);
                    displayMaxMin(f_Alist);
                    displayMag(f_Alist);
                }
            }
        });

        viewSwitcher = findViewById(R.id.vwSwitch);
        if (viewSwitcher == null) {
            Toast.makeText(getApplicationContext(), "Null ViewSwicther", Toast.LENGTH_LONG);

        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.bringToFront();
        navigation.setOnNavigationItemSelectedListener(navListener);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item)
                {
                    switch (item.getItemId())
                    {
                        case (R.id.navigation_home):
                            viewSwitcher.showNext();
                            // item.setEnabled(false);
                            item.getIcon().setAlpha(130);

                            break;
                        case R.id.more_details:
                            viewSwitcher.showPrevious();
                            //  item.setEnabled(false);


                            if (R.id.more_details == item.getItemId()) {

                            }
                            break;
                        case R.id.filter:
                            Intent i = new Intent(getApplicationContext(), Filter_eq.class);
                            startActivity(i);
                            break;
                    }
                    return false;
                }
            };



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

    public void displayMaxMin(LinkedList<ItemClass> f_Alist) {

        ArrayList<ItemClass> itemClassArrayList = new ArrayList<>(f_Alist);
        Collections.sort(itemClassArrayList, ItemClass.compareDepth);

        //depth Shallowest
        textTitle = findViewById(R.id.location2);
        textTitle.setText(itemClassArrayList.get(0).getLocation());

        textTitle = findViewById(R.id.coordinate2);
        textTitle.setText(itemClassArrayList.get(0).getLat() + " , " + itemClassArrayList.get(0).getLng());

        textTitle = findViewById(R.id.depth2);
        textTitle.setText((Double.toString(itemClassArrayList.get(0).getDepth())));

        textTitle = findViewById(R.id.mag2);
        textTitle.setText((Double.toString(itemClassArrayList.get(0).getMagnitude())));

        textTitle = findViewById(R.id.date2);
        textTitle.setText(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(itemClassArrayList.get(0).getPubDate()));


        //depth Deepest
        int maxIndex = itemClassArrayList.size() - 1;
        textTitle = findViewById(R.id.location3);
        textTitle.setText(itemClassArrayList.get(maxIndex).getLocation());

        textTitle = findViewById(R.id.coordinates3);
        textTitle.setText(itemClassArrayList.get(maxIndex).getLat() + " , " + itemClassArrayList.get(maxIndex).getLng());

        textTitle = findViewById(R.id.depth3);
        textTitle.setText((Double.toString(itemClassArrayList.get(maxIndex).getDepth())));

        textTitle = findViewById(R.id.mag3);
        textTitle.setText((Double.toString(itemClassArrayList.get(maxIndex).getMagnitude())));

        textTitle = findViewById(R.id.date3);
        textTitle.setText(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(itemClassArrayList.get(maxIndex).getPubDate()));
    }

    private void displayMag(LinkedList<ItemClass> f_Alist) {
        ArrayList<ItemClass> itemClassMagList = new ArrayList<>(f_Alist);
        Collections.sort(itemClassMagList, ItemClass.compareMag);

        // Display the largest magnitude
        int maxIndex = itemClassMagList.size() - 1;
        textTitle = findViewById(R.id.location1);
        textTitle.setText(itemClassMagList.get(maxIndex).getLocation());

        textTitle = findViewById(R.id.coordinates1);
        textTitle.setText(itemClassMagList.get(maxIndex).getLat() + " , " + itemClassMagList.get(maxIndex).getLng());

        textTitle = findViewById(R.id.depth1);
        textTitle.setText((Double.toString(itemClassMagList.get(maxIndex).getDepth())));

        textTitle = findViewById(R.id.mag1);
        textTitle.setText((Double.toString(itemClassMagList.get(maxIndex).getMagnitude())));

        textTitle = findViewById(R.id.date1);
        textTitle.setText(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").format(itemClassMagList.get(maxIndex).getPubDate()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng glasgow = new LatLng(55.8642, 4.2518);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 1));
        googleMap.setTrafficEnabled(true);
        googleMap.getMinZoomLevel();
        googleMap.setTrafficEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        clusterManager = new ClusterManager<ItemClass>(this, googleMap);

        CustomClusterRenderer renderer = new CustomClusterRenderer(this, googleMap, clusterManager);
        clusterManager.setRenderer(renderer);

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        // Position the map.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.8642, -5.2518), 4));
    }

    public void show(LinkedList<ItemClass> itemClassLinkedList) {

        for (int i = 0; i < itemClassLinkedList.size(); i++) {

            double lat = itemClassLinkedList.get(i).getLat();
            double lng = itemClassLinkedList.get(i).getLng();
            String Title = itemClassLinkedList.get(i).getLocation();

            String Magnitude = String.valueOf(itemClassLinkedList.get(i).getMagnitude() + " Depth: " + itemClassLinkedList.get(i).getDepth() + " Date: " + itemClassLinkedList.get(i).getPubDate());
            ItemClass offsetItem = new ItemClass(lat, lng, "" + Title, "Magnitude:" + Magnitude, itemClassLinkedList.get(i).getMagnitude());
            clusterManager.addItem(offsetItem);
        }
        clusterManager.cluster();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onClick(View view) {
        if (f_Alist != null) {

            switch (view.getId()) {
                case (R.id.Northerly):
                    filterListByCompass("N");
                    break;
                case (R.id.Southerly):
                    filterListByCompass("S");
                    break;
                case (R.id.Easterly):
                    filterListByCompass("E");
                    break;
                case (R.id.Westerly):
                    filterListByCompass("W");
                    break;
            }
        } else {
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Please select date range")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }
                    );
            AlertDialog alert = builder.create();
            //   alert.setTitle("Please select date range?");
            alert.show();


        }

    }

    private void filterListByCompass(String filterBy) {
        LinkedList<ItemClass> compassList = new LinkedList<>();
        for (int i = 0; i < f_Alist.size(); i++) {
            String[] compass = getCompass(f_Alist.get(i).getLat(), f_Alist.get(i).getLng());
            // Use LatDegree for this
            assert compass != null;
            switch (filterBy) {
                case "N":
                    if (compass[0].equals("N")) {
                        compassList.add(f_Alist.get(i));

                    }
                    break;
                case "S":
                    if (compass[0].equals("S")) {
                        compassList.add(f_Alist.get(i));
                    }
                    break;
                case "E":
                    if (compass[1].equals("E")) {
                        compassList.add(f_Alist.get(i));
                    }
                    break;
                case "W":
                    if (compass[1].equals("W")) {
                        compassList.add(f_Alist.get(i));
                    }
                    break;

            }
        }

        assert compassList != null;
        mMap.clear();
        clusterManager.clearItems();
        show(compassList);
    }

    private String[] getCompass(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "E" : "W";

            return new String[]{latDegree, lonDegrees};
        } catch (Exception e) {
            return null;
        }
    }

}
