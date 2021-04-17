package org.me.gcu.British_GSurvey_EQuake.model;
/* Mobile Platform Development Assignment Session 2020/2021 MHI322959/MHI325105
    Student Name : Kirubel Afework
*   Student No: S1732434
*/

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Comparator;
import java.util.Date;

public class ItemClass implements ClusterItem, Parcelable {


    private String title;
    private String description;
    private String link;
    private Date pubDate;
    private String location;
    private double magnitude;
    private double depth;
    private double lat;
    private double lng;
    private  String snippet;
    private  LatLng position;

    public static Comparator<ItemClass> compareDepth = new Comparator<ItemClass>() {
        @Override
        public int compare(ItemClass o1, ItemClass o2) {
            Double o1Depth = o1.getDepth();
            Double o2Depth = o2.getDepth();

            return o1Depth.compareTo(o2Depth);
        }
    };

    public static Comparator<ItemClass> compareMag = new Comparator<ItemClass>() {
        @Override
        public int compare(ItemClass o1, ItemClass o2) {
            Double o1Depth = o1.getMagnitude();
            Double o2Depth = o2.getMagnitude();

            return o1Depth.compareTo(o2Depth);
        }
    };
    public ItemClass()
    {
        title = "";
        description = "";
        link = "";
     //   pubDate ="";
        location= "";
      //  depth="";
      //  magnitude="";
        // lat;
       // lng;
    }

    public ItemClass(String title, String description, String link, Date pubDate, String location, double magnitude, double  depth, double lat, double lng, String snippet) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.location = location;
        this.magnitude = magnitude;
        this.depth = depth;
        this.lat = lat;
        this.lng = lng;
        this.snippet = snippet;
        position = new LatLng(lat, lng);
    }
    public ItemClass(double lat, double lng, String title, String snippet,double magnitude) {
        position = new LatLng(lat, lng);

        this.title = title;
        this.snippet = snippet;
        this.magnitude=magnitude;
    }
    public ItemClass(LatLng latLng) {
        position = new LatLng(lat, lng);
    }

    protected ItemClass(Parcel in) {
        title = in.readString();
        description = in.readString();
        link = in.readString();
        location = in.readString();
        magnitude = in.readDouble();
        depth = in.readDouble();
        lat = in.readDouble();
        lng = in.readDouble();
        snippet = in.readString();
        position = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<ItemClass> CREATOR = new Creator<ItemClass>() {
        @Override
        public ItemClass createFromParcel(Parcel in) {
            return new ItemClass(in);
        }

        @Override
        public ItemClass[] newArray(int size) {
            return new ItemClass[size];
        }
    };

    @NonNull
    @Override
    public LatLng getPosition() {        return position;   }

    public String getTitle()    {        return title;    }

    public void setSnippet(String snippet) {        this.snippet = snippet;    }

    public void setPosition(LatLng position) {        this.position = position;    }

    @Nullable
    @Override
    public String getSnippet() {       return snippet;   }

    public void setTitle(String title)    {        this.title = title;    }

    public String getDescription()    {        return description;    }

    public void setDescription(String description)    {        this.description = description;    }

    public String getLink()    {        return link;    }

    public void setLink(String link)    {        this.link = link;    }

    public Date getPubDate() {  return pubDate;   }

    public void setPubDate(Date  pubDate) {  this.pubDate = pubDate;  }

    public double getLat() {   return lat;    }

    public double getLng() {   return lng;    }

    public void setLat(double lat) {     this.lat = lat;    }

    public void setLng(double lng) {     this.lng = lng;    }

    public String getLocation() {    return location;    }

    public void setLocation(String location) {        this.location = location;    }

    public double getMagnitude() {        return magnitude;    }

    public void setMagnitude(double magnitude) {        this.magnitude = magnitude;    }

    public double getDepth() {        return depth;    }

    public void setDepth(double depth) {        this.depth = depth;    }

    public String toString()    {        String temp;        temp = title + " " + description + " " + link;  return temp;    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {

        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(link);
       //dest.writeString(String.valueOf(pubDate));
        dest.writeString(location);
        dest.writeDouble(magnitude);
        dest.writeDouble(depth);
        dest.writeDouble(lat);
        dest.writeDouble(lng);


    }
}

