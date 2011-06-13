package at.poimanager;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.android.maps.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import at.poimanager.sql.ReadFromSQLite;
import at.poimanager.sql.WriteToSQLite;

public class MapActivityMain extends MapActivity implements LocationListener{
	
	private MapView mapView;

	private POIItemizedOverlay placesItemizedOverlay;
	private static double EARTH_RADIUS_KM = 6384;// km
	private Location curLocation;
	private ReadFromSQLite readDB;
	private WriteToSQLite writeDB;
	//location
	private boolean gps_on = false;
	private LocationManager lm;
	private String provider;
	
    View.OnTouchListener gestureListener;
	
    private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	//POI Liste
	PoiSortedList poiList = new PoiSortedList();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Resources r = getResources();
    	placesItemizedOverlay = new POIItemizedOverlay(this,r.getDrawable(R.drawable.pushpin));
    	placesItemizedOverlay.populateNow();
    	readDB = new ReadFromSQLite(this.getApplicationContext());
    	writeDB = new WriteToSQLite(this.getApplicationContext());
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        // Get the location manager
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//check if GPS is activated
		//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,1.0f, this);
		
		// Define the criteria how to select the locatioin provider
		Criteria criteria = new Criteria();
		provider = lm.getBestProvider(criteria, false);
		curLocation = lm.getLastKnownLocation(lm.getBestProvider(criteria, false));
		if(curLocation!=null){
			curLocation.setLatitude(48.303056);
			curLocation.setLongitude(14.290556);
			Log.i("GPS","last known Location: "+curLocation.getLatitude()+","+curLocation.getLongitude());
		}
		else{
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,1.0f, this);
			curLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
        //Load POI's
        /*writeDB.insert(48.303056, 14.290556, "Linz", "In Linz beginnt's");
        writeDB.close();*/
        initialiseOverlays();
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    private void initialiseOverlays() {
    	placesItemizedOverlay.clear();
    	PoiSortedList poiList = readDB.selectAll();
    	Iterator<POI> i = poiList.getIterator();
    	System.out.println("Anzahl POIs: "+poiList.getSize());
    	while(i.hasNext()){
    		POI poi = (POI)i.next();
    		Double latitude = curLocation.getLatitude()*1E6;
    	    Double longitude = curLocation.getLongitude()*1E6;
    	    GeoPoint locationPoint = new GeoPoint(latitude.intValue(),longitude.intValue());
    		poi.calcDistance(locationPoint);
    		GeoPoint pos = poi.getGeoPoint();
    		System.out.println(poi.getTitle()+" an Position "+ pos.getLatitudeE6() +","+pos.getLongitudeE6() + "ist "+poi.getDistance()+" Kilometer vom derz. Standpunkt entfernt.");
    		
    		placesItemizedOverlay.addOverlayItem(new OverlayItem(new GeoPoint(
    	            (int) (pos.getLatitudeE6()), (int) (pos.getLongitudeE6())), poi.getTitle(),
    	            poi.getText()));
    	}
    	readDB.close();
        // Add the overlays to the map
        mapView.getOverlays().add(placesItemizedOverlay);
        System.out.println("Anzahl Overlay Elemente: "+placesItemizedOverlay.size());
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GeoPoint p;
		// Handle item selection
        switch (item.getItemId()) {
        case R.id.add:
        	
        	Projection projection = mapView.getProjection();
        	int y = mapView.getHeight() / 2; 
        	int x = mapView.getWidth() / 2;

        	p = projection.fromPixels(x, y);
                
                //create new Intent
                Intent addIntent = new Intent(MapActivityMain.this, AddPOI.class);
                addIntent.setAction(Intent.ACTION_VIEW);
                addIntent.putExtra("Lat",  p.getLatitudeE6()  / 1E6);
                addIntent.putExtra("Lng",  p.getLongitudeE6() / 1E6);
                startActivity(addIntent);
            return true;
        case R.id.delete:
            writeDB.deleteAll();
            writeDB.close();
            writeDB = new WriteToSQLite(this.getApplicationContext());
            initialiseOverlays();
            return true;
        case R.id.list:
        	Intent listIntent = new Intent(MapActivityMain.this, POIList.class);
            listIntent.setAction(Intent.ACTION_VIEW);
            Double latitude = curLocation.getLatitude();
    	    Double longitude = curLocation.getLongitude();
    	    Log.i("POIList","cur. Loc. "+latitude+","+longitude);
            listIntent.putExtra("curPosLat",  latitude);
            listIntent.putExtra("curPosLng",  longitude);
            startActivity(listIntent);
        case R.id.gps:
        	if(!gps_on){
        		//Location Manager
                lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, (LocationListener) this);
                MenuItem gps = (MenuItem) findViewById(R.id.gps);
                //gps.setIcon(R.drawable.gps);
                gps_on = true;
        	}
        	else if(gps_on){
        		// Remove the listener you previously added
        		lm.removeUpdates(this);
        		gps_on = false;
        		MenuItem gps = (MenuItem) findViewById(R.id.gps);
        		//gps.setIcon(R.drawable.gps_off);
        	}
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	initialiseOverlays();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	// when our activity pauses, we want to remove listening for location updates
    	
    }
    
    @Override
    public void onDestroy(){
		super.onDestroy();
		lm.removeUpdates(this);
		gps_on = false;
		mapView.postInvalidate();
	}
    
    @Override
    public void onBackPressed(){
    	finish();
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int actionType = ev.getAction();
        switch (actionType) {
        case MotionEvent.ACTION_UP:
             {
                Projection proj = mapView.getProjection();
                GeoPoint loc = proj.fromPixels((int)ev.getX(), (int)ev.getY()); 
                String lat=Double.toString(loc.getLatitudeE6()/1E6);
                String lng=Double.toString(loc.getLongitudeE6()/1E6);

                //Toast toast = Toast.makeText(getApplicationContext(), lat+" , "+lng, Toast.LENGTH_LONG);
                //toast.show();
            }

        }

        return super.dispatchTouchEvent(ev);
    }
    
    /* This method is called when use position will get changed */
    public void onLocationChanged(Location location) {
    	Log.i("GPS","new location: "+location.getLatitude()+","+location.getLongitude());
    	//last known Location
    	LocationProvider locationProvider = lm.getProvider(LocationManager.GPS_PROVIDER);
    	/*Location lastKnownLocation = new Location(lm.GPS_PROVIDER);
    	lastKnownLocation.setLatitude(curLocation.getLatitudeE6());
    	lastKnownLocation.setLongitude(curLocation.getLongitudeE6());*/
    	boolean better = isBetterLocation(location, curLocation);
    	if(better){
    		if (location != null) {
    	    	double lat = location.getLatitude();
    	    	double lng = location.getLongitude();
    	    	curLocation.setLatitude(lat);
    	    	curLocation.setLongitude(lng);
    	    	MapController mc = mapView.getController();
    	    	Double latitude = lat*1E6;
        	    Double longitude = lng*1E6;
                Toast toast = Toast.makeText(getApplicationContext(), lat+" , "+lng, Toast.LENGTH_LONG);
                toast.show();
        	    GeoPoint p = new GeoPoint(latitude.intValue(),longitude.intValue());
    	    	mc.animateTo(p);
    		}
    	}

    }
    
    public boolean onPrepareOptionsMenu(final Menu menu){
    	if(!gps_on){
    		menu.findItem(R.id.gps).setIcon(R.drawable.gps_off);
    		menu.findItem(R.id.gps).setTitle("GPS OFF");
    	}
    	else{
    		menu.findItem(R.id.gps).setIcon(R.drawable.gps);
    		menu.findItem(R.id.gps).setTitle("GPS ON");
    	}
    	return true;
    }
    
    public void onProviderDisabled(String provider) {
    }
    public void onProviderEnabled(String provider) {
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
        	Log.i("GPS","better: "+true);
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
        	Log.i("GPS","better: "+true);
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
        	Log.i("GPS","better: "+false);
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
        	Log.i("GPS","better: "+true);
            return true;
        } else if (isNewer && !isLessAccurate) {
        	Log.i("GPS","better: "+true);
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
        	Log.i("GPS","better: "+true);
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}