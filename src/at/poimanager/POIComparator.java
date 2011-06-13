package at.poimanager;

import java.util.Comparator;

public class POIComparator implements Comparator<POI>{

	public int compare(POI poi1, POI poi2) {
		double dist1 = poi1.getDistance();
		double dist2 = poi2.getDistance();
		
		if(dist1>dist2)
			return +1;
		else if(dist2>dist1)
			return -1;
		else
			return 0;
	}


}
