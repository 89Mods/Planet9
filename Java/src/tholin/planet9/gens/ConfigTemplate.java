package tholin.planet9.gens;

import java.awt.Color;
import java.util.Random;

public class ConfigTemplate {
	
	public enum ConfigType {
		ASTEROID,
		GRAYMOON,
		HABITABLE,
		HABITABLE_OCEAN,
		GAS_GIANT,
		STAR,
	}
	
	//Common attributes
	protected String name,packName;
	protected double radius,mass;
	protected int rotationPeriod;
	
	protected String referenceBodyName;
	protected double inclination,eccentricity,SMA,LAN,AOP,meanAnomalyAtEpoch,epoch;
	protected Color nodeColor;
	
	protected String heightmap,colormap,normals,biomes;
	protected int deformity;
	
	protected String configTemplate;
	
	private ConfigType type;
	
	public ConfigTemplate() {
		
	}
	
	public ConfigTemplate(String name, String packName, double radius, double mass, int rotationPeriod, String referenceBodyName, double inclination, double eccentricity, double sMA, double lAN, double aOP, double meanAnomalyAtEpoch, double epoch, Color nodeColor, String configTemplate, ConfigType type) {
		super();
		this.name = name;
		this.packName = packName;
		this.radius = radius;
		this.mass = mass;
		this.rotationPeriod = rotationPeriod;
		this.referenceBodyName = referenceBodyName;
		this.inclination = inclination;
		this.eccentricity = eccentricity;
		this.SMA = sMA;
		this.LAN = lAN;
		this.AOP = aOP;
		this.meanAnomalyAtEpoch = meanAnomalyAtEpoch;
		this.epoch = epoch;
		this.nodeColor = nodeColor;
		this.configTemplate = configTemplate;
		this.heightmap = "";
		this.colormap = "";
		this.normals = "";
		this.biomes = "";
		this.type = type;
		this.deformity = 1000;
	}
	
	public String buildKopernicusConfig() {
		String config = new String(this.configTemplate);
		
		config = config.replaceAll("\\{config:planet_name\\}", this.name.replace(' ', '_'));
		config = config.replaceAll("\\{config:packName\\}", this.packName.replace(' ', '_'));
		config = config.replaceAll("\\{config:displayName\\}", this.name);
		config = config.replaceAll("\\{config:description\\}", this.toString().replace("\r\n", "\\\\n"));
		config = config.replaceAll("\\{config:radius\\}", Double.toString(this.radius));
		config = config.replaceAll("\\{config:mass\\}", Double.toString(this.mass));
		config = config.replaceAll("\\{config:rotationPeriod\\}", Integer.toString(this.rotationPeriod));
		config = config.replaceAll("\\{config:referenceBody\\}", this.referenceBodyName.replace(' ', '_'));
		config = config.replaceAll("\\{config:inclination\\}", Double.toString(this.inclination));
		config = config.replaceAll("\\{config:eccentricity\\}", Double.toString(this.eccentricity));
		config = config.replaceAll("\\{config:SMA\\}", Double.toString(this.SMA));
		config = config.replaceAll("\\{config:LAN\\}", Double.toString(this.LAN));
		config = config.replaceAll("\\{config:AOP\\}", Double.toString(this.AOP));
		config = config.replaceAll("\\{config:meanAnomalyAtEpoch\\}", Double.toString(this.meanAnomalyAtEpoch));
		config = config.replaceAll("\\{config:epoch\\}", Double.toString(this.epoch));
		
		config = putColorInConfig("\\{config:nodeColor\\}", this.nodeColor, config);
		
		Random rng = new Random();
		config = config.replaceAll("\\{config:random1\\}", Integer.toString(rng.nextInt(100000)));
		config = config.replaceAll("\\{config:random2\\}", Integer.toString(rng.nextInt(100000)));
		config = config.replaceAll("\\{config:random3\\}", Integer.toString(rng.nextInt(100000)));
		config = config.replaceAll("\\{config:random4\\}", Integer.toString(rng.nextInt(100000)));
		
		config = config.replaceAll("\\{config:heightmap\\}", this.heightmap);
		config = config.replaceAll("\\{config:colormap\\}", this.colormap);
		config = config.replaceAll("\\{config:normals\\}", this.normals);
		config = config.replaceAll("\\{config:biomes\\}", this.biomes);
		config = config.replaceAll("\\{config:deformity\\}", Integer.toString(this.deformity));
		
		return config;
	}
	
	private static String putColorInConfig(String toReplace, Color c, String config) {
		return config.replaceAll(toReplace, "RGBA(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ",255)");
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public double getMass() {
		return mass;
	}
	
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public double getInclination() {
		return inclination;
	}
	
	public void setInclination(double inclination) {
		this.inclination = inclination;
	}
	
	public double getEccentricity() {
		return eccentricity;
	}
	
	public void setEccentricity(double eccentricity) {
		this.eccentricity = eccentricity;
	}
	
	public double getSMA() {
		return SMA;
	}
	
	public void setSMA(double sMA) {
		SMA = sMA;
	}
	
	public double getLAN() {
		return LAN;
	}
	
	public void setLAN(double lAN) {
		LAN = lAN;
	}
	
	public double getAOP() {
		return AOP;
	}
	
	public void setAOP(double aOP) {
		AOP = aOP;
	}
	
	public double getMeanAnomalyAtEpoch() {
		return meanAnomalyAtEpoch;
	}
	
	public void setMeanAnomalyAtEpoch(double meanAnomalyAtEpoch) {
		this.meanAnomalyAtEpoch = meanAnomalyAtEpoch;
	}
	
	public double getEpoch() {
		return epoch;
	}
	
	public void setEpoch(double epoch) {
		this.epoch = epoch;
	}
	
	public Color getNodeColor() {
		return nodeColor;
	}
	
	public void setNodeColor(Color nodeColor) {
		this.nodeColor = nodeColor;
	}
	
	public String getHeightmap() {
		return heightmap;
	}
	
	public void setHeightmap(String heightmap) {
		this.heightmap = heightmap;
	}
	
	public String getColormap() {
		return colormap;
	}
	
	public void setColormap(String colormap) {
		this.colormap = colormap;
	}
	
	public String getNormals() {
		return this.normals;
	}
	
	public void setNormals(String normals) {
		this.normals = normals;
	}
	
	public String getBiomes() {
		return this.biomes;
	}
	
	public void setBiomes(String biomes) {
		this.biomes = biomes;
	}
	
	public int getDeformity() {
		return this.deformity;
	}
	
	public void setDeformity(int deformity) {
		this.deformity = deformity;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getPackName() {
		return this.packName;
	}
	
	public String getReferenceBodyName() {
		return this.referenceBodyName;
	}
	
	public String getConfigTemplate() {
		return this.configTemplate;
	}
	
	public ConfigType getConfigType() {
		return this.type;
	}
	
	public String toString() {
		String res = "Name: " + this.name + "\r\n";
		res += "Pack Name: " + this.packName + "\r\n";
		res += "Type: " + this.type + "\r\n";
		res += "Radius: " + this.radius + "m\r\n";
		res += "Mass: " + this.mass + "kg\r\n";
		res += "Rotation period: " + this.rotationPeriod + "s\r\n";
		res += "Reference Body: " +  this.referenceBodyName + "\r\n";
		res += "Semi-major axis: " + this.SMA + "m\r\n";
		res += "Inclination: " + this.inclination + "°\r\n";
		res += "Eccentricity: " + this.eccentricity + "\r\n";
		res += "Longitude of ascending node: " + this.LAN + "°\r\n";
		res += "Argument of periapsis: " + this.AOP + "\r\n";
		res += "Mean anomaly at epoch: " + this.meanAnomalyAtEpoch + "\r\n";
		res += "Epoch: " + this.epoch + "\r\n";
		res += "Node Color: RGBA(" + this.nodeColor.getRed() + "," + this.nodeColor.getGreen() + "," + this.nodeColor.getBlue() + ",255)\r\n";
		return res;
	}
	
}