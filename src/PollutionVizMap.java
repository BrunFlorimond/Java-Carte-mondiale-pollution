import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.MultiFeature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
//import parsing.ParseFeed;
import processing.core.PApplet;

/** PollutionVizMap
 * An application with an interactive map displaying CO2 emission of countries.
 * @author Florimond BRUN
 * Date: October 30, 2015
 * */


public class PollutionVizMap extends PApplet {


		//Initialisation of the object map
		private UnfoldingMap map;
		private String displayedData = "CO2EmissionsPPP";
		
		/**************
		 *Data Sources*
		 **************/
		
		//Country
		private String countryFile = "countries.geo.json";
		private List<Marker> multiMarkers;		
		private List<Feature> countries;
		private HashMap<String, List<Float>> data = new HashMap<String, List<Float>>();

		
		public void setup() {
			
			multiMarkers = new ArrayList<Marker>();
			
			size(900, 700, OPENGL);
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			MapUtils.createDefaultEventDispatcher(this, map);
			
			//Setting countries
			countries = GeoJSONReader.loadData(this, countryFile);
			countries.forEach(m->{
				
				if(m instanceof MultiFeature){
					
					Marker newMarker = new CountryMarker();
					((CountryMarker) newMarker).setMarkers(toListOfMarker((MultiFeature) m));
					newMarker.setProperties(m.getProperties());
					
					multiMarkers.add((CountryMarker) newMarker);
					
				} else if (m instanceof ShapeFeature){
					
					Marker newMarker = new CountryMarker();
					((CountryMarker) newMarker).setMarkers(toSimplePolygonMarker((ShapeFeature) m));
					newMarker.setProperties(m.getProperties());
					
					multiMarkers.add((CountryMarker) newMarker);
					
				}
					
			});
			
			data = parseCSV (this, "data_WB.csv");
			storeToCountry(multiMarkers,data);
			
			CountryMarker test = (CountryMarker) multiMarkers.get(1);
			System.out.println(test.getCountryName() + " : " + test.getCO2Emissions());
			
			//multiMarkers.forEach(m -> m.setColor(color(255,0,0,75)));
			//Setting color of country
			
			
			
			map.addMarkers(multiMarkers);
			

			
					
			
		}
		
		public void draw() {
			
			background(0);
			
			for (Marker marker : multiMarkers){
				
				double value = 0;
				int colorLevel = 0;
				
				
			
				if(displayedData == "CO2Emissions"){
					
						value = ((CountryMarker) marker).getCO2Emissions();
						colorLevel = (int) map(((float)Math.log(value)),((float)Math.log(51.338)),((float)Math.log(9019518.22)),0,255);
					
				} else if (displayedData == "CO2EmissionsCap"){
					
						value = ((CountryMarker) marker).getCO2EmissionsCap();
						colorLevel = (int) map(((float)Math.log(value)),((float)Math.log(2.134992606)),((float)Math.log(4401.892637)),0,255);

					
				} else if (displayedData == "CO2EmissionsPPP") {
					
						value = ((CountryMarker) marker).getCO2EmissionsPPP();
						colorLevel = (int) map(((float)Math.log(value)),((float)Math.log(1.821556145)),((float)Math.log(129.377853)),((float)0),((float)255));

				}
				
				if(value != 0){
					
				
				marker.setColor(color(colorLevel,255-colorLevel,255-colorLevel));
				
				} else 
					
				{marker.setColor(color(255,255,50));}
				
				
			}

			map.draw();
			
			addKey();
			
			
			
		}
		@Override
		public void mouseClicked(){
			
			isInsideButton(25,25+150,50,50+25,"CO2Emissions");
			isInsideButton(25,25+150,80,80+25,"CO2EmissionsCap");
			isInsideButton(25,25+150,110,110+25,"CO2EmissionsPPP");
			isInCountry(mouseX,mouseY,multiMarkers);

			
			
		}
		
		private void isInsideButton(float xmin, float xmax, float ymin, float ymax,String data){
			
			if(mouseX < xmax && mouseX > xmin && mouseY < ymax && mouseY > ymin){
				
				this.displayedData = data;
				
			}
			
		}
		
		private List<Marker> toSimplePolygonMarker (ShapeFeature feature){
			
			
			List<Marker> listOfPolygonMarker = new ArrayList<Marker>();
			
			SimplePolygonMarker newMarker = new SimplePolygonMarker(feature.getLocations());
			
			listOfPolygonMarker.add(newMarker);
			
			
			return listOfPolygonMarker;
			
		}
		
		private List<Marker> toListOfMarker(MultiFeature feature){
			
			List<Marker> listMarker = new ArrayList<Marker>();
			
			
			List<Feature> listFeatures = feature.getFeatures();
			
			
			listFeatures.forEach( m -> {
				
				listMarker.add(new SimplePolygonMarker(((ShapeFeature) m).getLocations()));
				
			});
			
			return listMarker;
					
			
		}
		
		private HashMap<String,List<Float>> parseCSV (PApplet p, String fileName){
			
			HashMap<String,List<Float>> data = new HashMap<String,List<Float>>();
			
			String[] rows = p.loadStrings(fileName);
			
			for (String row : rows){
				
				String[] columns = row.split(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				List<Float> values = new ArrayList <Float>();
								
				for(int i = 0;i < 3;i++){
					
					if(columns[i+2].isEmpty()){values.add((float) 0.0);} else {values.add(Float.parseFloat(columns[i+2]));}
									
				}
					
					data.put(columns[0], values);
	
				
			}
			
			return data;
			
			
			
		}
		
		private void storeToCountry (List<Marker> markers,HashMap<String,List<Float>> data){
			
			for (Marker marker : markers){
				
				String countryName = ((CountryMarker) marker).getCountryName();
				
				
				if(data.get(countryName) != null){
					
					((CountryMarker) marker).setCO2Emissions(data.get(countryName).get(0));
					((CountryMarker) marker).setCO2EmissionsCap(data.get(countryName).get(1));
					((CountryMarker) marker).setCO2EmissionsPPP(data.get(countryName).get(2));
					
				} else {
					
					((CountryMarker) marker).setCO2Emissions((float) 0.0);
					((CountryMarker) marker).setCO2EmissionsCap((float) 0.0);
					((CountryMarker) marker).setCO2EmissionsPPP((float) 0.0);
					
				}
				

				
			}
			
		}
		
		private void addKey(){
			int xbase = 25;
			int ybase = 150;
			fill(255, 250, 240);
			rect(xbase, ybase, 150, 250);
			
			textAlign(LEFT, CENTER);
			textSize(12);
			
			fill(150, 150, 150);
			if(this.displayedData == "CO2Emissions"){
				fill(150, 150, 255);
				rect(25, 50, 150, 25);
				fill(150, 150, 150);
				rect(25, 80, 150, 25);			
				rect(25, 110, 150, 25);
				fill(0);
				text("CO2 Emissions \n(kt)", xbase+25, ybase+25);

				
				//key
				fill(255,0,0);
				rect(xbase +5,ybase +55,30,20);
				fill(0,255,255);
				rect(xbase +5, ybase +95,30,20);
				fill(255,255,50);
				rect(xbase +5, ybase +125,30,20);
				fill(0);
				text("+ 9 000 000 kt", xbase+45, ybase+65);
				text("51 kt", xbase+45, ybase+105);
				text("No data", xbase+45, ybase+135);
				
			} else if (this.displayedData == "CO2EmissionsCap"){
				
				fill(150, 150, 255);
				rect(25, 80, 150, 25);
				fill(150, 150, 150);
				rect(25, 50, 150, 25);		
				rect(25, 110, 150, 25);
				fill(255,255,50);
				rect(xbase +5, ybase +125,30,20);
				fill(0);
				text("CO2 Emissions \n(tons per capita)", xbase+25, ybase+25);
				
				//key
				fill(255,0,0);
				rect(xbase +5,ybase +55,30,20);
				fill(0,255,255);
				rect(xbase +5, ybase +95,30,20);
				fill(0);
				text("44 t per cap", xbase+45, ybase+65);
				text("0.02 t per cap", xbase+45, ybase+105);
				text("No data", xbase+45, ybase+135);
								
			} else if (this.displayedData == "CO2EmissionsPPP"){
				
				fill(150, 150, 255);
				rect(25, 110, 150, 25);
				fill(150, 150, 150);
				rect(25, 50, 150, 25);
				rect(25, 80, 150, 25);
				fill(0);
				text("CO2 Emissions \n(kg per PPP $\nof GDP)", xbase+25, ybase+25);
				
				//key
				
				fill(255,0,0);
				rect(xbase +5,ybase +55,30,20);
				fill(0,255,255);
				rect(xbase +5, ybase +95,30,20);
				fill(0,0,0);
				fill(255,255,50);
				rect(xbase +5, ybase +125,30,20);
				fill(0);
				text("1.29 kg per PPP", xbase+45, ybase+65);
				text("0.018 kg per PP", xbase+45, ybase+105);
				text("No data", xbase+45, ybase+135);
					
			}

			fill(0,0,0);
			text("CO2 emissions (kg/PPP)", 28, 122);
			text("CO2 emissions (tons/cap)", 28, 92);
			text("CO2 emissions (kt)", 28, 62);
			
	
		}
		
		private void isInCountry(int x, int y, List<Marker> multiMarkers) {
			// getting location of feature
			

			
			for (Marker multiMarker : multiMarkers){
				
				for (Marker marker : ((MultiMarker) multiMarker).getMarkers()){
					
					
					((CountryMarker) multiMarker).displayKey(false);
					if(marker.isInside(map, x, y)){						
						
						((CountryMarker) multiMarker).displayKey(true);	
						
						System.out.println(((CountryMarker) multiMarker).getCountryName());
						
					}
					
				}
				
			}
			



		}
		

		
		
		



}
