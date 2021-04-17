package org.me.gcu.British_GSurvey_EQuake.controller;

import android.util.Log;

import org.me.gcu.British_GSurvey_EQuake.MainActivity;
import org.me.gcu.British_GSurvey_EQuake.model.ItemClass;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Task implements Runnable {
    private String result = "";
    private String url;

    MainActivity  mainActivity;

    public Task(String aurl, MainActivity  mainActivity) {
        url = aurl;
          this.mainActivity = mainActivity;
    }

    @Override
    public void run() {

        URL aurl;
        URLConnection yc;
        BufferedReader in = null;
        String inputLine = "";

        try {
   
            aurl = new URL(url);
            yc = aurl.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                result = result + inputLine;
            }
            list(result);

            in.close();
            // return result;
        } catch (IOException ae) {
            Log.e("MyTag", "ioexception in run");
        }

        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                //  mainActivity.rawDataDisplay.setText(result);
          mainActivity.setUpClusterer();

            }
        });
    }

    private void list(String result) {
        mainActivity.alist = parseData(this.result);
    }

    private LinkedList<ItemClass> parseData(String result) {

        ItemClass itemClass = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Found a start tag
                if (eventType == XmlPullParser.START_DOCUMENT) {

                } else if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("channel")) {
                       mainActivity.alist = new LinkedList<ItemClass>();
                    } else if (xpp.getName().equalsIgnoreCase("item")) {

                        itemClass = new ItemClass();
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        // Now just get the associated text
                        String temp = xpp.nextText();
                        // Do something with text

                        if (!(temp.equals("Recent UK earthquakes") || temp.equals("BGS Logo"))) {
                            itemClass.setTitle(temp);
                        }
                    } else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("link")) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text

                            if (!temp.equals("http://earthquakes.bgs.ac.uk/")) {
                                itemClass.setLink(temp);
                            }
                        } else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("description")) {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                // Do something with text

                                if (!temp.equals("Recent UK seismic events recorded by the BGS Seismology team")) {
                                    itemClass.setDescription(temp);
                                    String[] description = temp.split(";");
                                    itemClass.setLocation(description[1].split(":")[1]);

                                    itemClass.setDepth(Double.parseDouble(description[3].replace(" km","").split(":")[1]));
                                    itemClass.setMagnitude(Double.parseDouble(description[4].split(":")[1]));
                                }
                            } else
                                // Check which Tag we have
                                if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                    // Now just get the associated text
                                    String temp = xpp.nextText().trim();

                                    // Do something with text
                                  //  SimpleDateFormat formatterOut;
                                    Date date = null;

                                    try {
                                       date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss").parse(temp);
                                       

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    itemClass.setPubDate(date);
                                } else
                                    // Check which Tag we have
                                    if (xpp.getName().equalsIgnoreCase("lat")) {
                                        // Now just get the associated text
                                        String temp = xpp.nextText();
                                        // Do something with text

//                                        if (!temp.equals("Recent UK seismic events recorded by the BGS Seismology team")) {
                                        itemClass.setLat(Double.parseDouble(temp));
//                                        }
                                    } else
                                        // Check which Tag we have
                                        if (xpp.getName().equalsIgnoreCase("long")) {
                                            // Now just get the associated text
                                            String temp = xpp.nextText();
                                            // Do something with text

//                                            if (!temp.equals("Recent UK seismic events recorded by the BGS Seismology team")) {
                                            itemClass.setLng(Double.parseDouble(temp));
//                                            }
                                        }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {

                        mainActivity.alist.add(itemClass);

                    } else if (xpp.getName().equalsIgnoreCase("channel")) {
                        int size;
                        size =    mainActivity.alist.size();
                    }
                }

                // Get the next event
                eventType = xpp.next();

            } // End of while

        } catch (XmlPullParserException ae1) {
            Log.e("MyTag", "Parsing error" + ae1.toString());
        } catch (IOException ae1) {
            Log.e("MyTag", "IO error during parsing");
        }

        return mainActivity.alist;
    }


}

