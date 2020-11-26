package tholin.planet9.gens;

import java.awt.Color;
import java.awt.image.BufferedImage;

import theGhastModding.planetGen.generators.GeneratorResult;

public class QaDPlanetRenderer {
	
	private double radius;
	
	private double ambientLight = 0.15;
	private double stepResolution = 12.0;
	private double rotation = 0.0;
	
	private double[][] heightmap = null;
	private float[][][] parsedTexture = null;
	private double deformity;
	
	private Color lightColor = new Color(255, 250, 250);
	
	public QaDPlanetRenderer(double radius) {
		this.radius = radius;
	}
	
	private double heightAt(int x, int y) {
		x %= heightmap.length;
		y %= heightmap[0].length;
		if(x < 0) x = (heightmap.length - 1) + x;
		if(y < 0) y = (heightmap[0].length - 1) + y;
		return heightmap[x][y];
	}
	
	public double sampleHeightmap(double x, double y) {
		x += rotation / 360.0 * heightmap.length;
		x *= heightmap.length;
		y *= heightmap[0].length;
		x %= heightmap.length;
		y %= heightmap[0].length;
		if(x < 0) x = (heightmap.length - 1) + x;
		if(y < 0) y = (heightmap[0].length - 1) + y;
		int imgx = (int)x;
		int imgy = (int)y;
		double div1 = (x - imgx);
		double div2 = (y - imgy);
		double R1 = heightAt(imgx, imgy) * div1 + heightAt(imgx - 1, imgy) * (1.0 - div1);
		double R2 = heightAt(imgx, imgy - 1) * div1 + heightAt(imgx - 1, imgy - 1) * (1.0 - div1);
		double P = R1 * div2 + R2 * (1.0 - div2);
		
		return P * deformity;
	}
	
	private double normalCalcOffset;
	
	private double[] heightmapNormal(double x, double y) {
		normalCalcOffset = 1.0 / (heightmap.length * 2.0);
		double hs = deformity;
		double h1 = sampleHeightmap(x + normalCalcOffset, y) / hs;
		double h2 = sampleHeightmap(x, y + normalCalcOffset / 2.0) / hs;
		double h3 = sampleHeightmap(x - normalCalcOffset, y) / hs;
		double h4 = sampleHeightmap(x, y - normalCalcOffset / 2.0) / hs;
		
		double wx = -(h1 - h3);
		double wy = 1.0/hs;
		double wz = h2 - h4;
		
		double len = Math.sqrt(wx * wx + wy * wy + wz * wz);
		wx /= len;
		wy /= len;
		wz /= len;
		
		double cosX = Math.cos((y * 180.0 - 0.0)   * 0.017453292519943295);
		double sinX = Math.sin((y * 180.0 - 0.0)   * 0.017453292519943295);
		double cosY = Math.cos((x * 360.0 - 180.0) * 0.017453292519943295);
		double sinY = Math.sin((x * 360.0 - 180.0) * 0.017453292519943295);
		
		double wyn = wy * cosX - wz * sinX;
		wz = wy * sinX + wz * cosX;
		wy = wyn;
		
		double wxn = wx * cosY + wz * sinY;
		wz = -wx * sinY + wz * cosY;
		wx = wxn;
		
		return new double[] {wx, wy, wz};
	}
	
	private double sunX = 400000;
	private double sunZ = -400000.0;
	
	private double traceLight(double originX, double originY, double originZ, double lat, double lon) {
		
		double posX = originX;
		double posY = originY;
		double posZ = originZ;
		double toSunX = sunX - posX;
		double toSunY = -posY;
		double toSunZ = sunZ - posZ;
		double length = Math.sqrt(toSunX * toSunX + toSunY * toSunY + toSunZ * toSunZ);
		toSunX /= length;
		toSunY /= length;
		toSunZ /= length;
		
		double[] normal = heightmapNormal((lon + 90.0) / 360.0, lat / 180.0);
		
		length = toSunX * normal[0] + toSunY * normal[1] + toSunZ * normal[2];
		if(length < this.ambientLight) length = this.ambientLight;
		return length;
	}
	
	public void loadData(GeneratorResult res, double deformity) {
		this.heightmap = res.heightmapRaw;
		this.deformity = deformity;
		this.parsedTexture = new float[res.colorMap.getWidth()][res.colorMap.getHeight()][3];
		for(int i = 0; i < res.colorMap.getWidth(); i++) {
			for(int j = 0; j < res.colorMap.getHeight(); j++) {
				this.parsedTexture[i][j][0] = ((res.colorMap.getRGB(i, j) >> 16) & 0xFF) / 255.0f;
				this.parsedTexture[i][j][1] = ((res.colorMap.getRGB(i, j) >> 8) & 0xFF) / 255.0f;
				this.parsedTexture[i][j][2] = ((res.colorMap.getRGB(i, j) >> 0) & 0xFF) / 255.0f;
			}
		}
	}
	
	private double altitudeOver(double x, double y, double z) {
		//x -= posX;
		//y -= posY;
		//z -= posZ;
		double dist = Math.sqrt(x * x + y * y + z * z);
		x /= dist;
		y /= dist;
		z /= dist;
		
		double latitude = Math.acos(y) * 57.29577951308232;
		double longitude = ((270 + (Math.atan2(x , z)) * 180 / Math.PI) % 360) -180;
		
		return sampleHeightmap((longitude + 90.0) / 360.0, latitude / 180.0);
	}
	
	private static final double FOV = 0.65;
	
	public void render(BufferedImage img) {
		double toImageX,toImageY,toImageZ,length,posX,posY,posZ,vectx,vecty,vectz,distx,disty,distz,t0,t1,dist0,dist1,endx,endy,endz;
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j = 0; j < img.getHeight(); j++) {
				toImageX = 2.0 * (double)i / (double)img.getWidth() - 1.0;
				toImageX *= FOV;
				toImageY = -(2.0 * (double)j / (double)img.getWidth() - 1.0);
				toImageY *= FOV;
				toImageZ = 1;
				
				length = Math.sqrt(toImageX * toImageX + toImageY * toImageY + toImageZ * toImageZ);
				toImageX /= length;
				toImageY /= length;
				toImageZ /= length;
				
				final double sphereRadius = radius + deformity + 0.5;
				final double cPos = -sphereRadius * 2.0;
				
				double[] a = raySphereIntersection(0, 0, 0, 0, 0, cPos, toImageX, toImageY, toImageZ, sphereRadius);
				if(a == null) {
					img.setRGB(i, j, 0);
					continue;
				}
				t0 = Math.max(a[0], 0.0);
				t1 = Math.max(a[1], 0.0);
				
				//Take whichever point is closer to the camera (since that's the one it can actually see)
				dist0 = Math.sqrt(t0 * toImageX * t0 * toImageX + t0 * toImageY * t0 * toImageY + t0 * toImageZ * t0 * toImageZ);
				dist1 = Math.sqrt(t1 * toImageX * t1 * toImageX + t1 * toImageY * t1 * toImageY + t1 * toImageZ * t1 * toImageZ);
				double isectClose = t0;
				double isectFar = t1;
				if(dist1 < dist0) {
					isectClose = t1;
					isectFar = t0;
				}
				
				posX = isectClose * toImageX + 0;
				posY = isectClose * toImageY + 0;
				posZ = isectClose * toImageZ + cPos;
				vectx = isectFar * toImageX - isectClose * toImageX;
				vecty = isectFar * toImageY - isectClose * toImageY;
				vectz = isectFar * toImageZ - isectClose * toImageZ;
				length = Math.sqrt(vectx * vectx + vecty * vecty + vectz * vectz);
				vectx /= length;
				vecty /= length;
				vectz /= length;
				vectx /= stepResolution;
				vecty /= stepResolution;
				vectz /= stepResolution;
				
				endx = 0 - (isectFar * toImageX + 0);
				endy = 0 - (isectFar * toImageY + 0);
				endz = 0 - (isectFar * toImageZ + cPos);
				double enddist = Math.sqrt(endx * endx + endy * endy + endz * endz) * 1.05;
				
				while(true) {
					distx = 0 - posX;
					disty = 0 - posY;
					distz = 0 - posZ;
					double dist = Math.sqrt(distx * distx + disty * disty + distz * distz);
					if(dist >= enddist) {
						img.setRGB(i, j, 0);
						break;
					}
					
					double val = altitudeOver(posX, posY, posZ);
					if(dist <= radius + val) {
						double[] latlong = latlong(posX, posY, posZ);
						double latitude = latlong[0];
						double longitude = latlong[1];
						float[] col = sampleTexture((longitude + 90.0) / 360.0, latitude / 180.0);
						
						double l = traceLight(posX, posY, posZ, latitude, longitude);
						l = Math.max(Math.min(l, 1.0), 0.0);
						//l = 1.0;
						col[0] *= l * (lightColor.getRed() / 255.0);
						col[1] *= l * (lightColor.getGreen() / 255.0);
						col[2] *= l * (lightColor.getBlue() / 255.0);
						
						int r = (int)(col[0] * 255.0);
						int g = (int)(col[1] * 255.0);
						int b = (int)(col[2] * 255.0);
						if(r > 255) r = 255;
						if(g > 255) g = 255;
						if(b > 255) b = 255;
						if(r < 0) r = 0;
						if(g < 0) g = 0;
						if(b < 0) b = 0;
						
						img.setRGB(i, j, b | (g << 8) | (r << 16));
						break;
					}
					
					posX += vectx;
					posY += vecty;
					posZ += vectz;
				}
			}
		}
	}
	
	private float[] textureAt(double x, double y) {
		int imgx = (int)x;
		int imgy = (int)y;
		imgx %= parsedTexture.length;
		imgy %= parsedTexture[0].length;
		if(imgx < 0) imgx = (parsedTexture.length - 1) + imgx;
		if(imgy < 0) imgy = (parsedTexture[0].length - 1) + imgy;
		return parsedTexture[imgx][imgy];
	}
	
	private float[] sampleTexture(double x, double y){
		x += rotation / 360.0 * heightmap.length;
		x *= parsedTexture.length;
		y *= parsedTexture[0].length;
		x %= parsedTexture.length;
		y %= parsedTexture[0].length;
		if(x < 0) x = (parsedTexture.length - 1) + x;
		if(y < 0) y = (parsedTexture[0].length - 1) + y;
		float div1 = ((float)x - (int)(x));
		float div2 = ((float)y - (int)(y));
		float[] colorsAt11 = textureAt(x, y);
		float[] colorsAt21 = textureAt(x - 1, y);
		float[] colorsAt12 = textureAt(x, y - 1);
		float[] colorsAt22 = textureAt(x - 1, y - 1);
		
		float R1r = colorsAt11[0] * div1 + colorsAt21[0] * (1.0f - div1);
		float R1g = colorsAt11[1] * div1 + colorsAt21[1] * (1.0f - div1);
		float R1b = colorsAt11[2] * div1 + colorsAt21[2] * (1.0f - div1);
		
		float R2r = colorsAt12[0] * div1 + colorsAt22[0] * (1.0f - div1);
		float R2g = colorsAt12[1] * div1 + colorsAt22[1] * (1.0f - div1);
		float R2b = colorsAt12[2] * div1 + colorsAt22[2] * (1.0f - div1);
		
		float P1 = R1r * div2 + R2r * (1.0f - div2);
		float P2 = R1g * div2 + R2g * (1.0f - div2);
		float P3 = R1b * div2 + R2b * (1.0f - div2);
		
		return new float[] {P1, P2, P3};
	}
	
	public static double[] raySphereIntersection(double posX, double posY, double posZ, double origX, double origY, double origZ, double dirX, double dirY, double dirZ, double radius) {
		double Lx = posX - origX;
		double Ly = posY - origY;
		double Lz = posZ - origZ;
		
		double tca = dirX * Lx; //Tca = L * O
		tca += dirY * Ly;
		tca += dirZ * Lz;
		double L_2 = Lx * Lx + Ly * Ly + Lz * Lz;
		if(tca <= 0 && Math.sqrt(L_2) > radius) return null;
		
		//D = sqrt(L*L - tca * tca)
		double d = L_2 - tca * tca;
		if(Math.sqrt(d) >= radius) return null;
		
		//thc = sqrt(radius² - d²)
		double thc = radius * radius - d;
		thc = Math.sqrt(thc);
		
		double t0 = tca - thc;
		double t1 = tca + thc;
		return new double[] {t0, t1};
	}
	
	public static double[] latlong(double x, double y, double z) {
		double dist = Math.sqrt(x * x + y * y + z * z);
		x /= dist;
		y /= dist;
		z /= dist;
		
		double latitude = Math.acos(y) * 57.29577951308232;
		double longitude = ((270 + (Math.atan2(x , z)) * 180 / Math.PI) % 360) -180;
		
		return new double[] {latitude, longitude};
	}
	
}