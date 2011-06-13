package at.poimanager.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import at.poimanager.POI;
import at.poimanager.PoiSortedList;

import com.google.android.maps.GeoPoint;

public class ReadFromSQLite {
	
	private SQLiteDatabase db;
	private SQLitePOIHelper helper;
	private Cursor cur;
	
	public ReadFromSQLite(Context context){
		helper = new SQLitePOIHelper(context);
		this.db = helper.getReadableDatabase();
	}
	
	 public PoiSortedList selectAll() {
		  cur = this.getAllDBCursor();
		  PoiSortedList poiList = new PoiSortedList();
		  //if results found
		  if(cur != null && cur.getCount() > 0){
			  
			  int latColumn  = cur.getColumnIndex("lat");
			  int lngColumn  = cur.getColumnIndex("lng");
			  int titleColumn = cur.getColumnIndex("title");
			  int textColumn = cur.getColumnIndex("text");
			  Log.i("createList","LatIndex: "+latColumn);
			  Log.i("createList","LngIndex: "+lngColumn);
			  Log.i("createList","TitleIndex: "+titleColumn);
			  Log.i("createList","TextIndex: "+textColumn);
			  double lat = 0.0;
			  double lng = 0.0;
			  String title = "";
			  String text = "";
			  Log.i("createList","Cursor Elemente: "+cur.getCount());
	        		for(cur.moveToFirst();cur.moveToNext();cur.isAfterLast()) {
	        			Log.i("createList",title+": "+text);
	        			lat = Float.valueOf(cur.getString(latColumn));
	                    lng = Float.valueOf(cur.getString(lngColumn));
	                    title = cur.getString(titleColumn);
	                    text = cur.getString(textColumn);
	                    POI p = new POI(title,text,new GeoPoint((int) (lat * 1E6),(int) (lng * 1E6)), 0.0);
	                    poiList.addPoi(p);
	               }
	        		Log.i("createList","End selectAll");
	        		Log.i("createList",""+poiList.getSize());
		  }
	      return poiList;
	   }
	 
	 public Cursor getAllDBCursor(){
		 Log.i("cursor","Start selectAll");
		 if(!db.isOpen()){
			 db = helper.getReadableDatabase();
		 }
		  cur = this.db.query(SQLitePOIHelper.TABLE_NAME, new String[] { "_id,lat,lng,title,text" }, 
				   null, null, null, null, null);
		  return cur;
	 }
	 
	 public void close(){
		 if(!cur.isClosed()){
			 cur.close();
		 }
		 if(db.isOpen()){
			 db.close();
		 }
	 }
}
