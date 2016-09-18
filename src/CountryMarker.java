import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import processing.core.PGraphics;


public class CountryMarker extends MultiMarker  {
	
	private Float CO2Emissions;
	private Float CO2EmissionsPPP;
	private Float CO2EmissionsCap;
	private String name;
	private boolean displayKey = false;
	
	//Setters
	

	
	public void setCO2Emissions (Float CO2Em){
		
		this.CO2Emissions = CO2Em;
		
	}
	
	public void setCO2EmissionsPPP (Float CO2EmPPP){
		
		this.CO2EmissionsPPP = CO2EmPPP;
		
	}
	
	public void setCO2EmissionsCap (Float CO2EmCap){
		
		this.CO2EmissionsCap = CO2EmCap;
		
	}
	
	//Getters
	
	public double getCO2Emissions (){
		
		return this.CO2Emissions;
		
	}
	
	public double getCO2EmissionsPPP (){
		
		return this.CO2EmissionsPPP;
		
	}
	
	public double getCO2EmissionsCap (){
		
		return this.CO2EmissionsCap;
		
	}
	
	public String getCountryName(){
		
		return this.getStringProperty("name");
		
	}
	
	public void displayKey(boolean bool){
		
		this.displayKey = bool;
		
	}
	
	public boolean getDisplayed(){
		
		return this.displayKey;
	}

	public void draw(PGraphics pg){
		
		int xbase = 25;
		int ybase = 150;
			

			
		System.out.println("test");
		
		//pg.fill(0);
		//pg.text(this.getCountryName(), xbase+45, ybase+165);
			

		
	}
	
	
}
