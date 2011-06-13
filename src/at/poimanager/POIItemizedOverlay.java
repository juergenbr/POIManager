package at.poimanager;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Toast;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
 
public class POIItemizedOverlay extends ItemizedOverlay {
  private Context context;
  private ArrayList<OverlayItem> items;
  private GestureDetector gestureDetector;
 
  public POIItemizedOverlay(Context aContext, Drawable marker) {
    super(boundCenterBottom(marker));
    items = new ArrayList<OverlayItem>();
    context = aContext;
    
  }
 
    public void addOverlayItem(OverlayItem item) {
        items.add(item);
        populateNow();
    }
    
    public void clear(){
    	items.clear();
    }
 
    @Override
    protected OverlayItem createItem(int i) {
        return items.get(i);
    }
 
    @Override
    public int size() {
        return items.size();
    }
 
    @Override
    protected boolean onTap(int index) {
    	  OverlayItem item = items.get(index);
    	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    	  dialog.setTitle(item.getTitle());
    	  dialog.setMessage(item.getSnippet());
    	  dialog.show();
    	  return true;
    }
  
    public void populateNow(){
        populate();
    }
}
