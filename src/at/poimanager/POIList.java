package at.poimanager;

import java.util.Collections;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Projection;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import at.poimanager.sql.ReadFromSQLite;
import at.poimanager.sql.SQLitePOIHelper;
import at.poimanager.sql.WriteToSQLite;

public class POIList extends ListActivity {

    private Cursor myCur = null;
    private ReadFromSQLite reader;
    private WriteToSQLite writer;
	private Cursor cur;	
	private PoiSortedList list;
	private ListView lv;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reader = new ReadFromSQLite(this.getApplicationContext());
        writer = new WriteToSQLite(this.getApplicationContext());
        initList();
        	
        	/*
        	lv.setOnItemClickListener(new OnItemClickListener() {
        	    public void onItemClick(AdapterView<?> parent, View view,
        	        int position, long id) {
        	      // When clicked, show a toast with the TextView text
        	      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
        	          Toast.LENGTH_SHORT).show();
        	    }
        	  });
        	*/
    }
    
    public void initList(){
    	list = reader.selectAll();
        
        //get curPos
        Bundle bun = getIntent().getExtras();
		double latVal = bun.getDouble("curPosLat");
		double lngVal = bun.getDouble("curPosLng");
		
		GeoPoint geo = new GeoPoint((int) (latVal * 1E6), (int) (lngVal * 1E6));
		
		//calculate Distance
		list.calcDistance(geo);
        
		//sort List
		POIComparator comparator = new POIComparator();
		Collections.sort(list.getList(), comparator);
		
        //myCur = this.getAllDBCursor();
        	Log.i("POIList","Anzahl POIs: "+list.getSize());
        	setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list.getStringList()));
        	lv = getListView();
        	lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        	lv.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GeoPoint p;
		// Handle item selection
        switch (item.getItemId()) {
        case R.id.delete:
        	lv = this.getListView();
        	SparseBooleanArray selected = lv.getCheckedItemPositions();
        	Log.i("deletePOI","Elements in BoolenArray: "+selected.size());
        	for(int i=0;i<lv.getCount();i++){
        		Log.i("deletePOI","Position: "+i);
        		for(int j=0;j<selected.size();j++){
        			if(selected.get((int)lv.getItemIdAtPosition(i))){
        				Log.i("deletePOI","selected");
        				String str = (String)lv.getItemAtPosition(i);
        				String substring = str.substring(0, str.indexOf(" ("));
        				POI poi = list.findPOI(substring);
        				writer.removePOI(poi);
        			}
        			else
        				Log.i("deletePOI","not selected");
        		}
        		
        	}
        	writer.close();
    		ListAdapter la = lv.getAdapter();
    		initList();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void onDestroy(){
		super.onDestroy();
		if (writer != null) {
	        writer.close();
	    }
		if (reader != null) {
	        reader.close();
	    }
	}
}
