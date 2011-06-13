package at.poimanager.sql;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class SQLitePOIHelper extends SQLiteOpenHelper{
	
	public static final String DATABASE_NAME = "POIManager.db";
	public static final int DATABASE_VERSION = 7;
	public static final String TABLE_NAME = "poilist";
	private SQLiteDatabase db = null;
	private SQLiteStatement insertStmt;
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
    		+ " (_id integer AUTO_INCREMENT PRIMARY KEY, lat DOUBLE, lng DOUBLE, title varchar(255), text varchar(255));";
	
	private static final String INSERT = "insert into " 
	      + SQLitePOIHelper.TABLE_NAME + " ('lat', 'lng', 'title', 'text') values (?,?,?,?)";
	
	 public SQLitePOIHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
     }
	 
	 /*
	 public void openDB() throws SQLException{
		 Log.i("openDB","Checking if DB == null");
		 if(this.db == null){
			 Log.i("openDB", "Creating DB");
			 this.db = this.getWritableDatabase();
		 }
		 SQLiteDatabase.openOrCreateDatabase(db.getPath(), null);
	 }

	 public void closeDB(){
		 if(this.db != null){
			 if(this.db.isOpen()){
				 this.db.close();
			 }
		 }
	 }
	 */
	 
     @Override
     public void onCreate(SQLiteDatabase db) throws SQLException{
    	 if (!db.isOpen()) {
    		throw new IllegalStateException("database not open");
    	 }
        db.execSQL(DATABASE_CREATE);
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException{
    	 if (!db.isOpen()) {
     		throw new IllegalStateException("database not open");
     	 }
        Log.w("Example", "Upgrading database, this will drop tables and recreate.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
     }
     /*
     public Cursor getCursor() throws SQLException{
    	 if (!db.isOpen()) {
     		throw new IllegalStateException("database not open");
     	 }
  	   PoiSortedList poiList = new PoiSortedList();
  	   Cursor cur = this.db.query(SQLitePOIHelper.TABLE_NAME, new String[] { "_id,lat,lng,title,text" }, 
  	        null, null, null, null, null);
  	   System.out.println("getCursor Count: "+cur.getCount());
  	   return cur;
     }
     
     public PoiSortedList selectAll() throws SQLException{
    	 if (!db.isOpen()) {
     		throw new IllegalStateException("database not open");
     	 }
        PoiSortedList poiList = new PoiSortedList();
        Cursor c = this.db.query(SQLitePOIHelper.TABLE_NAME, new String[] { "lat,lng,title,text" }, 
          null, null, null, null, null);
        
        System.out.println("selectAll Cursor Count: "+c.getCount());
        int latColumn  = c.getColumnIndex("lat");
        int lngColumn  = c.getColumnIndex("lng");
        int titleColumn = c.getColumnIndex("title");
        int textColumn = c.getColumnIndex("text");
        c.moveToFirst();
        double lat = 0.0;
        double lng = 0.0;
        String title = "";
        String text = "";
          		do {
          			  lat = Float.valueOf(c.getString(latColumn));
                      lng = Float.valueOf(c.getString(lngColumn));
                      title = c.getString(titleColumn);
                      text = c.getString(textColumn);
                      POI p = new POI(title,text,new GeoPoint((int) (lat * 1E6),(int) (lng * 1E6)), 0.0);
                      poiList.addPoi(p);
                      System.out.println(title+": "+text);
                 } while(c.moveToNext());
        if (c != null && !c.isClosed()) {
           //c.close();
           //closeDB();
        }
        return poiList;
     }
     
     public long insert(double lat, double lng, String title, String text) throws SQLException{
    	 if (!db.isOpen()) {
     		throw new IllegalStateException("database not open");
     	 }
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

     public void deleteAll() throws SQLException{
    	 if (!db.isOpen()) {
     		throw new IllegalStateException("database not open");
     	 }
        this.db.delete(SQLitePOIHelper.TABLE_NAME, null, null);
     }*/
}
