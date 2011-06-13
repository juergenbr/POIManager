package at.poimanager;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class POI implements Comparable{
	
	
	
	private String title;
	private String text;
	private GeoPoint p;
	private double distance;
	private static double EARTH_RADIUS_KM = 6384;// km
	
	public POI(){
		title = "";
		setText("");
		p = new GeoPoint(0,0);
		distance = 0.0;
	}
	
	public POI(String title, String text, GeoPoint p, double distance){
		this.title = title;
		this.setText(text);
		this.p = p;
		this.distance = distance;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setDistance(double dist){
		distance = dist;
	}
	
	public GeoPoint getGeoPoint(){
		return p;
	}
	
	

	public int compareTo(Object p1) {
		POI p = (POI)p1;
		if(this.getDistance() > p.getDistance())
			return 1;
		else if(this.getDistance() == p.getDistance())
			return 0;
		else
			return -1;
	}
	
	public void calcDistance(GeoPoint curLocation){
    	if(p != null && curLocation != null){
    		double dist = calculateDistanceKilometers(p.getLatitudeE6()/ 1E6, p.getLongitudeE6()/ 1E6, curLocation.getLatitudeE6()/ 1E6, curLocation.getLongitudeE6()/ 1E6);
    		this.distance = dist;
    	}
	}
	
	private double calculateDistanceKilometers(double aLong, double aLat,
	         double bLong, double bLat) {

	      double d2r = (Math.PI / 180);

	      double dLat = (bLat - aLat) * d2r;
	      double dLon = (bLong - aLong) * d2r;
	      double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	            + Math.cos(aLat * d2r) * Math.cos(bLat * d2r)
	            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
	      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	      return EARTH_RADIUS_KM * c;

	   }

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	
}
