package org.me.gcu.British_GSurvey_EQuake.controller;

/* Mobile Platform Development Assignment Session 2020/2021 MHI322959/MHI325105
    Student Name : Kirubel Afework
*   Student No: S1732434
*/

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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


public class Filter_eq extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {


    private TextView textTitle;
    private Button dateRange;
    private TextView dateDisplay;
    private LinkedList<ItemClass> alist;
    LinkedList<ItemClass> f_Alist;

    private double min;
    public ClusterManager<ItemClass> clusterManager;


    private Marker markerPerth;
    private Marker markerSydney;
    private Marker markerBrisbane;
    private final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_eq);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.alist = new LinkedList<>(MainActivity.alist);
//        if (f_Alist == null || f_Alist.size() == 0) {
//
//            f_Alist==alist;
//        }

        textTitle = findViewById(R.id.location1);
        // textTitle.setText(findMax());


        dateRange = findViewById(R.id.date_Rangepicker);
        dateDisplay = findViewById(R.id.dateDisplay);

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
        rangeDate.setTitleText("select a date");
        //      builder.setSelection(today);

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
                //   dateDisplay.setText("Selected date" + materialDatePicker.getHeaderText());

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
                dateDisplay.setText("SELECTED DATE : " + simpleFormat.format(startDate) + " Second : " + simpleFormat.format(endDate));


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

                    show();
                }

            }
        });

        findMaxMin(alist);





    }


    public Double findMaxMin(LinkedList<ItemClass> alist) {
        ArrayList<Double> sortedlist = new ArrayList<>();
        // check list is empty or not
        if (alist == null || alist.size() == 0) {
            return Double.MIN_VALUE;
        }


        for (int i = 0; i < alist.size(); i++) {
            if (min > alist.get(i).getDepth()) {
                min = alist.get(i).getDepth();
            }
            sortedlist.add(alist.get(i).getDepth());

        }
        Collections.sort(sortedlist);

        textTitle = findViewById(R.id.depth3);
        textTitle.setText((Double.toString(sortedlist.get(0))));
        int maxIndex = sortedlist.size() - 1;


        textTitle = findViewById(R.id.depth2);
        textTitle.setText((Double.toString(sortedlist.get(maxIndex))));

        return min;
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
        //  show();
        // Position the map.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.8642, -5.2518), 4));
    }

    public void show() {

        for (int i = 0; i < f_Alist.size(); i++) {

            double lat = f_Alist.get(i).getLat();
            double lng = f_Alist.get(i).getLng();
            String Title = f_Alist.get(i).getLocation();

            String Magnitude = String.valueOf(f_Alist.get(i).getMagnitude() + f_Alist.get(i).getLng());
            ItemClass offsetItem = new ItemClass(lat, lng, "" + Title, "Magnitude:" + Magnitude, f_Alist.get(i).getMagnitude());
            clusterManager.addItem(offsetItem);

        }

        clusterManager.cluster();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


}
