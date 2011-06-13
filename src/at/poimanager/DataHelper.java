package at.poimanager;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import at.poimanager.sql.SQLitePOIHelper;

import com.google.android.maps.GeoPoint;

public class DataHelper {

   private SQLitePOIHelper openHelper = null;

   private Context context;
   private SQLiteDatabase db;
   private SQLiteStatement insertStmt;
   private static final String INSERT = "insert into " 
      + SQLitePOIHelper.TABLE_NAME + " ('lat', 'lng', 'title', 'text') values (?,?,?,?)";
   private Cursor c = null;
   private boolean CursorDest = true;
   
   public DataHelper(Context context) {
	   this.context = context;
	   openHelper = new SQLitePOIHelper(this.context);
	   this.db = openHelper.getWritableDatabase();
       this.insertStmt = this.db.compileStatement(INSERT);
   }

   public long insert(double lat, double lng, String title, String text) {
	   this.insertStmt.bindDouble(1, lat);
	   this.insertStmt.bindDouble(2, lng);
	   this.insertStmt.bindString(3, title);
	   this.insertStmt.bindString(4, text);try{
		   
		   long result =  this.insertStmt.executeInsert();
		   return result;
	   }
	   catch(SQLiteConstraintException e){
		   e.printStackTrace();
	   }
	   return 0;
	   
   }

   public void deleteAll() {
      this.db.delete(SQLitePOIHelper.TABLE_NAME, null, null);
   }
   
   public SQLitePOIHelper getHelper(){
	   return new SQLitePOIHelper(this.context);
   }

   public void constructCursor(){
	   Log.i("cursor","Start constructCursor");
	   destroyCursor();
	   if(c == null){
		   PoiSortedList poiList = new PoiSortedList();
		   this.c = this.db.query(SQLitePOIHelper.TABLE_NAME, new String[] { "_id,lat,lng,title,text" }, 
				   null, null, null, null, null);
		   this.CursorDest = false;
		   Log.i("cursor","cursor constructed: "+!this.CursorDest);
	   }
   }
   
   private void destroyCursor(){
	   Log.i("cursor","Start destroyCursor");
	   if(c != null){
			   c.close();
			   c = null;
			   this.CursorDest = true;
			   Log.i("cursor","cursor destroyed: "+this.CursorDest);
	   }
	   else{
		   this.CursorDest = false;
		   Log.i("cursor","cursor destroyed: "+this.CursorDest);
	   }
   }
   
   public Cursor getCursor(){
	   constructCursor();
	   return c;
   }
   
   public PoiSortedList selectAll() {
	  this.open();
	  Log.i("cursor","Start selectAll");
	  Cursor cur = getCursor();
      PoiSortedList poiList = new PoiSortedList();
      int latColumn  = cur.getColumnIndex("lat");
      int lngColumn  = cur.getColumnIndex("lng");
      int titleColumn = cur.getColumnIndex("title");
      int textColumn = cur.getColumnIndex("text");
      c.moveToFirst();
      double lat = 0.0;
      double lng = 0.0;
      String title = "";
      String text = "";
        		do {
        			lat = Float.valueOf(cur.getString(latColumn));
                    lng = Float.valueOf(cur.getString(lngColumn));
                    title = c.getString(titleColumn);
                    text = c.getString(textColumn);
                    POI p = new POI(title,text,new GeoPoint((int) (lat * 1E6),(int) (lng * 1E6)), 0.0);
                    poiList.addPoi(p);
                    System.out.println(title+": "+text);
               } while(cur.moveToNext());
      destroyCursor();
      Log.i("cursor","End selectAll");
      this.close();
      return poiList;
   }
   
   public DataHelper open() throws SQLException{
	   openHelper = new SQLitePOIHelper(context);
	   db = openHelper.getWritableDatabase();
	   return this;
   }
   
   public void close(){
	   openHelper.close();
   }
   
   
}