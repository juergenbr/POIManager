package at.poimanager.sql;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import at.poimanager.POI;

public class WriteToSQLite {
	private SQLiteDatabase db;
	private SQLitePOIHelper helper;
	
	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " 
	      + SQLitePOIHelper.TABLE_NAME + " ('lat', 'lng', 'title', 'text') values (?,?,?,?)";
	
	public WriteToSQLite(Context context){
		helper = new SQLitePOIHelper(context);
		this.db = helper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
	}
	
	public long insert(double lat, double lng, String title, String text) {
		if(!db.isOpen()){
			db = helper.getWritableDatabase();
		}
		this.insertStmt.bindDouble(1, lat);
		this.insertStmt.bindDouble(2, lng);
		this.insertStmt.bindString(3, title);
		this.insertStmt.bindString(4, text);
		try{
		   long result =  this.insertStmt.executeInsert();
		   return result;
		}
		catch(SQLiteException e){
			   e.printStackTrace();
		}
		   return 0;
		   
	}
	
	public void removePOI(POI p){
		if(!db.isOpen()){
			db = helper.getWritableDatabase();
		}
		String query = "DELETE FROM "+helper.TABLE_NAME +
			" WHERE title='"+p.getTitle()+"' AND text='"+p.getText()+"'";
		db.execSQL(query);
		close();
	}
	
	public void deleteAll() {
		if(!db.isOpen()){
			db = helper.getWritableDatabase();
		}
		String query = "DELETE FROM "+helper.TABLE_NAME;
		db.execSQL(query);
		query = "DROP TABLE IF EXISTS "+helper.TABLE_NAME;
		db.execSQL(query);
		helper.onCreate(db);
	}
	
	public void close(){
		if(db.isOpen()){
			db.close();
		}
	}
	
	public void open(){
		if(!db.isOpen()){
			db = helper.getWritableDatabase();
		}
	}
}
