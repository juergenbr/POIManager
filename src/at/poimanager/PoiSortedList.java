package at.poimanager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.GeoPoint;

public class PoiSortedList {
	private Collection<POI> list = new ArrayList<POI>();
	
	public void addPoi(POI p){
		this.list.add(p);
	}
	
	public POI[] getPoiArray(){
		POI[] array = (POI[])list.toArray();
		return array;
	}
	
	public String[] getStringArray(){
		Iterator<POI> it = list.iterator();
		int i = 0;
		String[] array= new String[list.size()];
		while(it.hasNext()){
			POI p = (POI) it.next();
			array[i] = p.getTitle();
			i++;
		}
		return array;
	}
	
	public List<POI> getList(){
		return (List<POI>) list;
	}
	
	public Iterator<POI> getIterator(){
		return list.iterator();
	}
	
	public int getSize(){
		return list.size();
	}
	
	public List<String> getStringList(){
		Iterator<POI> i = list.iterator();
		ArrayList<String> stringList = new ArrayList<String>();
		DecimalFormat f = new DecimalFormat("#0.00");
		while(i.hasNext()){
			POI p = (POI)i.next();
			stringList.add(p.getTitle()+" ("+f.format(p.getDistance())+" km)");
		}
		return stringList;
	}
	
	public void calcDistance(GeoPoint curLoc){
		Iterator<POI> i = list.iterator();
		ArrayList<POI> distList = new ArrayList<POI>();
		while(i.hasNext()){
			POI p = (POI)i.next();
			p.calcDistance(curLoc);
			distList.add(p);
		}
		list = distList;
	}
	
	public POI findPOI(String title){
		Iterator<POI> i = list.iterator();
		ArrayList<POI> distList = new ArrayList<POI>();
		while(i.hasNext()){
			POI p = (POI)i.next();
			if(p.getTitle().equalsIgnoreCase(title)){
				return p;
			}	
		}
		return null;
	}
}
