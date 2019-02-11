package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.core.Coordinate;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.Yahoo;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Philip Laxamana
 * Date: February 11, 2019
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// GUI Dimensions
	public static final int GUI_X = 1200;
	public static final int GUI_Y = 900;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(GUI_X, GUI_Y, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {  
			
			// Using Microsoft Maps since Google's map crashes after too many uses
			map = new UnfoldingMap(this, 250, 50, 900, 800, new Microsoft.RoadProvider());
			
			// To test with a local file, uncomment next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    for(PointFeature f: earthquakes) {
	    	SimplePointMarker marker = createMarker(f);
	    	markers.add(marker);
	    }
	    
	    
	    // Add the markers to the map so that they are displayed
	    map.addMarkers(markers);
	}
		
	private SimplePointMarker createMarker(PointFeature feature)
	{  
		// To print all of the features in a PointFeature (so you can see what they are)
		// uncomment the line below.  Note this will only print if you call createMarker 
		// from setup
		//System.out.println(feature.getProperties());
		
		// Create a new SimplePointMarker at the location given by the PointFeature
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		
		// Colors
		int blue = color(0, 0, 255);
		int red = color(255, 0, 0);
	    int yellow = color(255, 255, 0);
	    
	    // Minor earthquake x < 4.0 (blue/small)
	    // Light earthquakes 4.0 < x < 4.9 (yellow/medium)
	    // Moderate and higher earthquakes x > 5.0 (red/large)
	    
	    // Checks the magnitude of the earthquake
	    // magnitudes are distinguished by size and color 
	    if(mag < THRESHOLD_LIGHT) {
	    	marker.setColor(blue);
	    	marker.setRadius(5.0f);
	    } else if (mag < THRESHOLD_MODERATE) {
	    	marker.setColor(yellow);
	    	marker.setRadius(10.0f);
	    } else {
	    	marker.setColor(red);
	    	marker.setRadius(15.0f);
	    }
	    
	    // Finally return the marker
	    return marker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}

	// helper method to draw key in GUI
	private void addKey() 
	{	
		// rect(x, y, width, height, corner-radius);
		fill(255, 255, 204);
		rect(15, 50, 225, 800, 7);
		
		// Word texts
		String eqLegend = "Earthquake Key";
		String minorEq = "Below 4.0";
		String moderateEq = "4.0+ Magnitude";
		String largeEq = "5.0+ Magnitude";
		
		// Earthquake Key Title
		fill(0, 0, 0);
		textSize(20);
		text(eqLegend, 45, 75);
		
		// Large Earthquake
		fill(0, 0, 0);
		textSize(18);
		text(largeEq, 60, 125);
		fill(255, 0, 0);
		ellipse(45, 120, 20, 20);
		
		// Medium Earthquake
		fill(0, 0, 0);
		textSize(18);
		text(moderateEq, 60, 175);
		fill(255, 255, 0);
		ellipse(45, 170, 15, 15);
		
		// Large Earthquake
		fill(0, 0, 0);
		textSize(18);
		text(minorEq, 60, 225);
		fill(0, 0, 255);
		ellipse(45, 220, 10, 10);
		
	}
}
