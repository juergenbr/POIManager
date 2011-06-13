package at.poimanager;

import com.google.android.maps.MapView;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import at.poimanager.sql.WriteToSQLite;

public class AddPOI extends Activity {
	
	private EditText poiName;
	private EditText poiText;
	private EditText lat;
	private EditText lng;
	private Button addButton;
	private Button cancelButton;
	//gerändert 17-05-11
	private WriteToSQLite dh;
	
	public void onDestroy(){
		super.onDestroy();
		if (dh != null) {
	        dh.close();
	    }
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		dh = new WriteToSQLite(this.getBaseContext());
		setContentView(R.layout.addpoiactivity);
		
		poiName = (EditText)findViewById(R.id.POINameEdit);
		poiName.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				poiName.setText("");
				poiName.setLines(1);
				
			}
			
		});
		poiText = (EditText)findViewById(R.id.POITextEdit);
		poiText.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				poiText.setText("");
				poiText.setLines(1);
				
			}
			
		});
		lat = (EditText)findViewById(R.id.LatEdit);
		lat.setEnabled(false);
		lng = (EditText)findViewById(R.id.LngEdit);
		lng.setEnabled(false);
		
		addButton = (Button)findViewById(R.id.AddButton);
		this.addButton.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				String title = poiName.getText().toString();
				String text = poiText.getText().toString();
				double latD = Double.parseDouble(lat.getText().toString());
				double lngD = Double.parseDouble(lng.getText().toString());
				dh.open();
				long result = dh.insert(latD, lngD, title, text);
				System.out.println(result);
				finish();
			}
			
		});
		
		cancelButton = (Button)findViewById(R.id.CancelButton);
		this.cancelButton.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				finish();
			}
			
		});
		//fill in coordinates
		Bundle bun = getIntent().getExtras();
		double latVal = bun.getDouble("Lat");
		double lngVal = bun.getDouble("Lng");
		
		lat.setText(Double.toString(latVal));
		lng.setText(Double.toString(lngVal));
	}
}
