package org.me.gcu.British_GSurvey_EQuake;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import org.me.gcu.British_GSurvey_EQuake.model.Adapter;
import org.me.gcu.British_GSurvey_EQuake.model.CustomClusterRenderer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements OnClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private static final String[] paths = {"All Earthquakes", "1.0+ Earthquakes", "2.5+ Earthquakes", "4.5+ Earthquakes", "Significant Earthquakes"};

    private TextView rawDataDisplay;
    private Button startButton;
    private String result = "";
    private String url1 = "";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    RecyclerView recyclerView;
    Adapter adapter;


    private ClusterManager<ItemClass> clusterManager;
    LinkedList<ItemClass> alist = null;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    // private MapsActivity mapsActivity = new MapsActivity();
    private GoogleMap mMap;
    //   private static final LatLng glasgow = new LatLng(55.860916, -4.251433);
    //   private static LatLng melbourneLocation;

    private LatLng try1 = new LatLng(-27.47093, 153.0235);
    // protected LatLng mCenterLocation = new LatLng( 39.7392, -104.9903 );

    private Marker mtry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //  mapsActivity.onCreate(savedInstanceState);
        //  setContentView(R.layout.content_main);
        Log.e("MyTag", "in onCreate");
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        Log.e("MyTag", "after startButton");
        // More Code goes here

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mMap);
        mapFragment.getMapAsync(this);

        spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onClick(View aview) {
        Log.e("MyTag", "in onClick");
        mMap.clear();
        startProgress();

        //    show();
        Log.e("MyTag", "after startProgress");
    }

    public void startProgress() {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }


    private class Task implements Runnable {
        private String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag", "after ready");
                //
                // Now read the data. Make sure that there are no specific hedrs
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                //
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                list(result);
                in.close();
                // return result;
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    //       rawDataDisplay.setText(result);

                    setUpClusterer();
                }
            });

        }
    }

    private void list(String result) {
        alist = parseData(this.result);

        // Write list to Log for testing
        if (alist != null) {
            Log.e("MyTag", "List not null");
            for (Object o : alist) {
                Log.e("MyTag", o.toString());
            }
        } else {
            Log.e("MyTag", "List is null");
        }

    }

    public void show() {

        for (int i = 0; i < alist.size(); i++) {

            double lat = alist.get(i).getLat();
            double lng = alist.get(i).getLng();
            String Title = alist.get(i).getLocation();

            String Magnitude = String.valueOf(alist.get(i).getMagnitude() + alist.get(i).getLng());


            ItemClass offsetItem = new ItemClass(lat, lng, "" + Title, "Magnitude:" + Magnitude, alist.get(i).getMagnitude());
            clusterManager.addItem(offsetItem);


//            // below line is use to move our camera to the specific location.
            // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(try1, 4));
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, alist);
        recyclerView.setAdapter(adapter);

        clusterManager.cluster();
    }

    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<ItemClass>(this, mMap);

        CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, clusterManager);
        clusterManager.setRenderer(renderer);


        //   clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(this)));

        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        show();

        // Position the map.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.8642, 4.2518), 3));

    }

    private LinkedList<ItemClass> parseData(String result) {

        ItemClass itemClass = null;
        // LinkedList<ItemClass> alist = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Found a start tag
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    // Check which Tag we have
//                    if (xpp.getName().equalsIgnoreCase("rss"))
//                    {
//
//                    }
//                    else
                    if (xpp.getName().equalsIgnoreCase("channel")) {
                        alist = new LinkedList<ItemClass>();
                    } else if (xpp.getName().equalsIgnoreCase("item")) {
                        Log.e("MyTag", "Item Start Tag found");
                        itemClass = new ItemClass();
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text
                        Log.e("MyTag", "Title is " + temp);
                        if (!(temp.equals("Recent UK earthquakes") || temp.equals("BGS Logo"))) {
                            itemClass.setTitle(temp);
                        }
                    } else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("link")) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            Log.e("MyTag", "Link is " + temp);
                            if (!temp.equals("http://earthquakes.bgs.ac.uk/")) {
                                itemClass.setLink(temp);
                            }
                        } else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("description")) {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                // Do something with text
                                Log.e("MyTag", "Description is " + temp);
                                if (!temp.equals("Recent UK seismic events recorded by the BGS Seismology team")) {
                                    itemClass.setDescription(temp);
                                    String[] description = temp.split(";");
                                    itemClass.setLocation(description[1].split(":")[1]);
                                    itemClass.setDepth(description[3].split(":")[1]);
                                    itemClass.setMagnitude(Double.parseDouble(description[4].split(":")[1]));
                                }
                            } else
                                // Check which Tag we have
                                if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                    // Now just get the associated text
                                    String temp = xpp.nextText();
                                    // Do something with text
                                    Log.e("MyTag", "Published Date  is " + temp);
//                                    if (!temp.equals("Recent UK seismic events recorded by the BGS Seismology team")) {
                                    itemClass.setPubDate(temp);
//                                    }
                                } else
                                    // Check which Tag we have
                                    if (xpp.getName().equalsIgnoreCase("lat")) {
                                        // Now just get the associated text
                                        String temp = xpp.nextText();
                                        // Do something with text
                                        Log.e("MyTag", "geo:lat  is " + temp);
//                                        if (!temp.equals("Recent UK seismic events recorded by the BGS Seismology team")) {
                                        itemClass.setLat(Double.parseDouble(temp));
//                                        }
                                    } else
                                        // Check which Tag we have
                                        if (xpp.getName().equalsIgnoreCase("long")) {
                                            // Now just get the associated text
                                            String temp = xpp.nextText();
                                            // Do something with text
                                            Log.e("MyTag", "geo:long is " + temp);
//                                            if (!temp.equals("Recent UK seismic events recorded by the BGS Seismology team")) {
                                            itemClass.setLng(Double.parseDouble(temp));
//                                            }
                                        }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        Log.e("MyTag", "itemClass is " + itemClass.toString());
                        alist.add(itemClass);
                        // clusterManager.addItems(alist);
                        //     clusterManager.cluster();
                    } else if (xpp.getName().equalsIgnoreCase("channel")) {
                        int size;
                        size = alist.size();
                        Log.e("MyTag", "channel  size is " + size);
                    }
                }

                // Get the next event
                eventType = xpp.next();

            } // End of while

            //return alist;
        } catch (XmlPullParserException ae1) {
            Log.e("MyTag", "Parsing error" + ae1.toString());
        } catch (IOException ae1) {
            Log.e("MyTag", "IO error during parsing");
        }

        Log.e("MyTag", "End document");

        return alist;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng glasgow = new LatLng(55.8642, 4.2518);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 0));
//        mMap.getMinZoomLevel();
//        mMap.setTrafficEnabled(true);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(glasgow));
        //mMap.addMarker(new MarkerOptions().position(glasgow).title("Glasgow"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 5));

        mMap.getMinZoomLevel();
        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setRotateGesturesEnabled(true);
//        mMap.getUiSettings().setTiltGesturesEnabled(true);


        enableMyLocation(mMap); // Enable location tracking.
        //  setMapLongClick(mMap); // Set a long click listener for the map;

        googleMap.setOnInfoWindowClickListener(this);

    }


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

    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    public boolean onMarkerClick(Marker marker) {
        //    Log.d(MainActivity.TAG, "onMarkerClick() called with: marker = [" + marker.getTitle() + "]");
        return false;
    }

}