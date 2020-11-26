package tholin.planet9.gens;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import edu.cornell.lassp.houle.RngPack.RanMT;
import theGhastModding.planetGen.generators.AsteroidMoonGen;
import theGhastModding.planetGen.generators.GeneratorResult;
import theGhastModding.planetGen.generators.GraymoonGen;
import theGhastModding.planetGen.noise.OctaveNoise3D;
import theGhastModding.planetGen.noise.OctaveWorley;
import theGhastModding.planetGen.utils.CraterDistributer;
import theGhastModding.planetGen.utils.MapUtils;

public class SystemGenerator {
	
	public static String plugindataPath = "../PluginData";
	private final String packName = "Planet9";
	
	private GraymoonGen.GraymoonGenSettings p9Settings;
	private long p9Seed = 3526426346L;
	
	private GraymoonGen.GraymoonGenSettings[] majorMoons;
	private long[] majorMoonSeeds;
	private String[] majorMoonNames = new String[] {"Temi", "Fol", "Pandunia", "Спутник", "Goda"};
	
	private int minorMoonCount;
	private long minorMoonOrbitsSeed,minorMoonShapeSeed;
	private List<ConfigTemplate> minorMoonKopConfigs;
	private List<AsteroidMoonGen.AsteroidGenSettings> minorMoonGenConfigs;
	private boolean configsPrepared = false;
	
	private static final int[] p9MajorMoonRadii = new int[] {233000, 305000, 379000, 114000, 120000}; //3118 -> 2352 -> 4001 -> 463 -> 124
	private static final String[] majorMoonInternalNames = new String[] {"3118", "2352", "4001", "463", "124"};
	private static final double minorMoonPeri = 200000000.0;
	private static final double minorMoonApo = 50000000000.0;
	private static final double minorMoonMinmass = 5e14;
	private static final double minorMoonMaxmass = 3e17;
	private static final double minorMoonMindensity = 13496.328;
	private static final double minorMoonMaxdensity = 28740.754;
	private final String minorMoonTemplate;
	
	public SystemGenerator(int minorMoonCount) {
		this.minorMoonCount = minorMoonCount;
		this.p9Settings = new GraymoonGen.GraymoonGenSettings();
		loadDefaultP9Settings();
		this.majorMoons = new GraymoonGen.GraymoonGenSettings[5];
		this.majorMoonSeeds = new long[this.majorMoons.length];
		Random rng = new Random(); //TODO: Remove fixed seed once I'm done testing this.
		for(int i = 0; i < majorMoons.length; i++) majorMoonSeeds[i] = rng.nextLong();
		minorMoonOrbitsSeed = rng.nextLong();
		minorMoonShapeSeed = rng.nextLong();
		this.minorMoonKopConfigs = new ArrayList<ConfigTemplate>();
		this.minorMoonGenConfigs = new ArrayList<AsteroidMoonGen.AsteroidGenSettings>();
		
		String s = "";
		try {
			s = Files.readString(new File("minorMoonTemplate.cfg_").toPath());
		}catch(Exception e) {
			JOptionPane.showMessageDialog(GenMain.frame, "Error loading minor moon Kopernicus config template: " + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
		minorMoonTemplate = s;
	}
	
	public void prepareInitialConfigs() {
		if(configsPrepared) {
			System.out.println("[WARNING]: Something tried to call prepareInitialConfigs on the same instance of SystemGenerator more then once. This is most likely a bug.");
			return;
		}
		configsPrepared = true;
		for(int i = 0; i < majorMoons.length; i++) {
			majorMoons[i] = randomGraymoonSettings(majorMoonSeeds[i], p9MajorMoonRadii[i]);
			if(i == 1) {
				majorMoons[i].normalColor = new double[] {0.757, 0.819, 0.814};
				majorMoons[i].mariasColor = new double[] {0.4989237533816855, 0.5399744419510646, 0.536816696676497};
			}
		}
		genAsteroidOrbits();
	}
	
	public void genAsteroidOrbits() {
		minorMoonKopConfigs.clear();
		Random rng = new Random(minorMoonOrbitsSeed);
		for(int i = 0; i < minorMoonCount; i++) {
			double inclination,eccentricity,SMA,LAN,AOP;
			String name = Integer.toHexString(rng.nextInt(0x10000)).toUpperCase() + "x";
			double mass = CraterDistributer.biasFunction(Math.min(1, Math.abs(rng.nextGaussian() * 0.75)), 0.5) * (minorMoonMaxmass - minorMoonMinmass) + minorMoonMinmass;
			double density = rng.nextDouble() * (minorMoonMaxdensity - minorMoonMindensity) + minorMoonMindensity;
			double radius =  Math.pow(mass / density / (Math.PI * 4 / 3), 1.0/3.0);
			if(i == 0) {
				SMA = 1145950000;
				eccentricity = 0.995;
				inclination = 78;
				LAN = 90;
				AOP = 230;
			}else {
				inclination = 2.5 + CraterDistributer.biasFunction(rng.nextDouble(), 0.25) * 45.0 * (rng.nextBoolean() ? -1.0 : 1.0);
				eccentricity = Math.min(0.9, CraterDistributer.biasFunction(rng.nextDouble(), 0.5));
				double minSMA = -(minorMoonPeri / (eccentricity - 1.0));
				double maxSMA = minorMoonApo / (1.0 + eccentricity);
				SMA = CraterDistributer.biasFunction(rng.nextDouble(), 0.5) * (maxSMA - minSMA) + minSMA;
				LAN = rng.nextInt(360) + rng.nextDouble();
				AOP = rng.nextInt(360) + rng.nextDouble();
			}
			int g = rng.nextInt(255 - 64) + 64;
			ConfigTemplate conf = new ConfigTemplate(name, packName, radius, mass, 3000 + rng.nextInt(10000), "P9", inclination, eccentricity, SMA, LAN, AOP, rng.nextDouble(), 0, new Color(g, g, g), minorMoonTemplate, ConfigTemplate.ConfigType.ASTEROID);
			conf.setColormap("Planet9/PluginData/" + name + "_colors.png");
			conf.setHeightmap("Planet9/PluginData/" + name + "_height.png");
			conf.setNormals("Planet9/PluginData/" + name + "_normals.png");
			conf.setBiomes("Planet9/PluginData/" + name + "_biomes.png");
			conf.setDeformity((int)(10000 * (radius / 15000.0)));
			minorMoonKopConfigs.add(conf);
		}
		genAsteroidGeneratorConfigs(); //Configs need to be re-generated as part of that code is dependent on the asteroid's radius, which has now changed for all asteroids.
	}
	
	public void genAsteroidGeneratorConfigs() {
		minorMoonGenConfigs.clear();
		Random rng = new Random(minorMoonShapeSeed);
		for(int i = 0; i < minorMoonCount; i++) {
			minorMoonGenConfigs.add(randomAsteroidSettings(rng.nextLong(), (int)Math.ceil(minorMoonKopConfigs.get(i).getRadius())));
		}
	}
	
	public void changeConfigSeed(int idx, long newSeed) {
		majorMoonSeeds[idx] = newSeed;
		majorMoons[idx] = randomGraymoonSettings(majorMoonSeeds[idx], p9MajorMoonRadii[idx]);
		if(idx == 1) {
			majorMoons[idx].normalColor = new double[] {0.757, 0.819, 0.814};
			majorMoons[idx].mariasColor = new double[] {0.4989237533816855, 0.5399744419510646, 0.536816696676497};
		}
	}
	
	public long getConfigSeed(int idx) {
		return majorMoonSeeds[idx];
	}
	
	public GraymoonGen.GraymoonGenSettings getConfig(int idx) {
		return majorMoons[idx];
	}
	
	public int getMajorMoonRadius(int idx) {
		return p9MajorMoonRadii[idx];
	}
	
	public long getMinorMoonOrbitsSeed() {
		return this.minorMoonOrbitsSeed;
	}
	
	public long getMinorMoonShapeSeed() {
		return this.minorMoonShapeSeed;
	}
	
	public void changeMinorMoonOrbitsSeed(long newSeed) {
		this.minorMoonOrbitsSeed = newSeed;
		genAsteroidGeneratorConfigs();
	}
	
	public void changeMinorMoonShapeSeed(long newSeed) {
		this.minorMoonShapeSeed = newSeed;
		genAsteroidGeneratorConfigs();
	}
	
	public int getMinorMoonCount() {
		return this.minorMoonCount;
	}
	
	public AsteroidMoonGen.AsteroidGenSettings getMinorMoonGenConfig(int idx){
		return this.minorMoonGenConfigs.get(idx);
	}
	
	public ConfigTemplate getMinorMoonKopConfig(int idx) {
		return this.minorMoonKopConfigs.get(idx);
	}
	
	public GraymoonGen.GraymoonGenSettings getP9Settings(){
		return this.p9Settings;
	}
	
	public long getP9Seed() {
		return this.p9Seed;
	}
	
	public void changeP9Seed(long seed) {
		this.p9Seed = seed;
	}
	
	public GeneratorResult genP9PreviewMaps() throws Exception {
		int origWidth = p9Settings.width;
		int origHeight = p9Settings.height;
		p9Settings.width = 512;
		p9Settings.height = 256;
		RanMT rng = new RanMT().seedCompletely(p9Seed);
		GeneratorResult res = GraymoonGen.generate(rng, p9Settings, true, false, false);
		p9Settings.width = origWidth;
		p9Settings.height = origHeight;
		return res;
	}
	
	public GeneratorResult genMajorMoonPreviewMaps(int idx) throws Exception {
		int origWidth = majorMoons[idx].width;
		int origHeight = majorMoons[idx].height;
		majorMoons[idx].width = 512;
		majorMoons[idx].height = 256;
		RanMT rng = new RanMT().seedCompletely(majorMoonSeeds[idx]);
		GeneratorResult res = GraymoonGen.generate(rng, majorMoons[idx], true, false, false);
		majorMoons[idx].width = origWidth;
		majorMoons[idx].height = origHeight;
		return res;
	}
	
	public void genMajorMoonMaps(int idx) throws Exception {
		RanMT rng = new RanMT().seedCompletely(majorMoonSeeds[idx]);
		GeneratorResult res = GraymoonGen.generate(rng, majorMoons[idx], true, false, false);
		ImageIO.write(res.heightmap16, "png", new File(plugindataPath + "/" + majorMoonInternalNames[idx] + "_height.png"));
		ImageIO.write(res.colorMap, "png", new File(plugindataPath + "/" + majorMoonInternalNames[idx] + "_colors.png"));
		ImageIO.write(res.biomeMap, "png", new File(plugindataPath + "/" + majorMoonInternalNames[idx] + "_biomes.png"));
		ImageIO.write(MapUtils.generateNormalMap(res.heightmapRaw, majorMoons[idx].planetRadius, 10000.0, 0.33333), "png", new File(plugindataPath + "/" + majorMoonInternalNames[idx] + "_normals.png"));
		File kopFile = new File(plugindataPath + "/../KopernicusConfigs/" + majorMoonInternalNames[idx] + ".cfg");
		String s = Files.readString(kopFile.toPath());
		s = s.replaceFirst("\t+displayMame = .+", "\t\tdisplayName = " + majorMoonNames[idx] + "^N");
		Files.writeString(kopFile.toPath(), s);
	}
	
	public GeneratorResult genMinorMoonPreviewMaps(int idx) throws Exception {
		AsteroidMoonGen.AsteroidGenSettings ags = minorMoonGenConfigs.get(idx);
		int origWidth = ags.width;
		int origHeight = ags.height;
		ags.width = 512;
		ags.height = 256;
		Random rng1 = new Random(minorMoonShapeSeed);
		long seed = rng1.nextLong();
		if(idx != 0) for(int i = 0; i < idx; i++) seed = rng1.nextLong();
		RanMT rng = new RanMT().seedCompletely(seed);
		GeneratorResult res = AsteroidMoonGen.generate(rng, ags, true, false, false);
		ags.width = origWidth;
		ags.height = origHeight;
		return res;
	}
	
	public void buildMinorMoonKopConfig(int idx) throws Exception {
		File kopernicusFolder = new File(new File(SystemGenerator.plugindataPath).getParentFile().getPath() + "/KopernicusConfigs/minor_moons");
		if(!kopernicusFolder.exists()) kopernicusFolder.mkdirs();
		File outfile = new File(kopernicusFolder.getPath() + "/" + minorMoonKopConfigs.get(idx).getName() + ".cfg");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		bw.write(minorMoonKopConfigs.get(idx).buildKopernicusConfig());
		bw.newLine();
		bw.close();
	}
	
	public void genMinorMoonMaps(int idx) throws Exception {
		Random rng1 = new Random(minorMoonShapeSeed);
		long seed = rng1.nextLong();
		if(idx != 0) for(int i = 0; i < idx; i++) seed = rng1.nextLong();
		RanMT rng = new RanMT().seedCompletely(seed);
		GeneratorResult res = AsteroidMoonGen.generate(rng, minorMoonGenConfigs.get(idx), true, false, false);
		String name = minorMoonKopConfigs.get(idx).getName();
		ImageIO.write(res.heightmap16, "png", new File(plugindataPath + "/" + name + "_height.png"));
		ImageIO.write(res.colorMap, "png", new File(plugindataPath + "/" + name + "_colors.png"));
		ImageIO.write(res.biomeMap, "png", new File(plugindataPath + "/" + name + "_biomes.png"));
		ImageIO.write(MapUtils.generateNormalMap(res.heightmapRaw, minorMoonGenConfigs.get(idx).planetRadius, 10000.0, 0.33333), "png", new File(plugindataPath + "/" + name + "_normals.png"));
	}
	
	public void genP9() throws Exception {
		if(!new File(plugindataPath).isDirectory()) throw new Exception("The PluginData directory was not found or is not a directory.");
		GeneratorResult res = GraymoonGen.generate(new Random(p9Seed), p9Settings, true, false, false);
		ImageIO.write(res.heightmap16, "png", new File(plugindataPath + "/P9_height.png"));
		ImageIO.write(res.colorMap, "png", new File(plugindataPath + "/P9_colors.png"));
		ImageIO.write(res.biomeMap, "png", new File(plugindataPath + "/P9_biomes.png"));
		ImageIO.write(MapUtils.generateNormalMap(res.heightmapRaw, 1068000, 10000, 1.1), "png", new File(plugindataPath + "/P9_normals.png"));
	}
	
	private void loadDefaultP9Settings() {
		p9Settings.width = GenMain.LOWMEM ? 6144 : 8192;
		p9Settings.height = GenMain.LOWMEM ? 3072 : 4096;
		p9Settings.planetRadius = 400000;
		p9Settings.smallCraterCount = 128;
		p9Settings.hugeCraterCount = 4;
		p9Settings.craterMaxsize = 64;
		p9Settings.craterMaxstrength = 0.3;
		p9Settings.mariaCraterMaxsize = 16;
		p9Settings.mariaCraterMaxstrength = 0.2;
		p9Settings.craterFlattenedStart = 20;
		p9Settings.mariaCraterCount = 1;
		p9Settings.mariaNoise.noise = new OctaveNoise3D(16, 16, 16, 7, 2.0, 0.67);
		p9Settings.mariaNoise.setDistortStrength(0.5).setNoiseOffset(0.15);
		p9Settings.mariaLatitudeRange = 91;
		p9Settings.mariaLongitudeRange = 181;
		p9Settings.secondColorNoise.noise = new OctaveNoise3D(16, 16, 16, 5, 2.0, 0.7);
		p9Settings.secondColorNoise.setDistortStrength(0.75).setNoiseOffset(0.325).setNoiseStrength(1.2);
		p9Settings.secondaryColor = MapUtils.RGB(new Color(170, 170, 170));
		p9Settings.biomeColorSecondary = MapUtils.RGB(Color.WHITE);
		p9Settings.mariasColor = MapUtils.RGB(new Color(38, 28, 25));
		p9Settings.normalColor = MapUtils.RGB(new Color(148, 110, 97));
		p9Settings.mountainsColor = MapUtils.RGB(new Color(114, 59, 43));
		double mScale = p9Settings.mountainNoise.noiseLatitudeScale * 0.85;
		p9Settings.mountainNoise.setNoiseLatitudeScale(mScale).setNoiseLongitudeScale(mScale * 0.85);
		p9Settings.groundNoiseLargeDetail.setNoiseScale(p9Settings.groundNoiseLargeDetail.noiseLatitudeScale * 0.25);
		p9Settings.groundNoiseMediumDetail.setNoiseScale(p9Settings.groundNoiseMediumDetail.noiseLatitudeScale * 0.25);
		p9Settings.groundNoiseSmallDetail.setNoiseScale(p9Settings.groundNoiseSmallDetail.noiseLatitudeScale * 0.25);
		p9Settings.groundNoiseLargeDetail.noiseStrength *= 0.8;
	}
	
	private GraymoonGen.GraymoonGenSettings randomGraymoonSettings(long seed, int radius/*double randomnizationStrength*/){
		//TODO: randomnize crater configs
		GraymoonGen.GraymoonGenSettings settings = new GraymoonGen.GraymoonGenSettings();
		Random rng = new Random(seed);
		settings.width = GenMain.LOWMEM ? 6144 : 8192;
		settings.height = GenMain.LOWMEM ? 3072 : 4096;
		settings.planetRadius = radius;
		double craterStrengthMul = (double)settings.planetRadius / 150000.0;
		if(craterStrengthMul > 1.25) craterStrengthMul = 1.25;
		settings.craterMaxstrength *= craterStrengthMul;
		settings.craterMinstrength *= craterStrengthMul;
		settings.mariaCraterMaxstrength *= craterStrengthMul;
		settings.mariaCraterMinstrength *= craterStrengthMul;
		if(rng.nextInt(3) == 0) {
			settings.mariaLongitudeRange = 181;
			settings.mariaLatitudeRange = 91;
			settings.mariaCraterCount = 3 + rng.nextInt(7);
		}else {
			settings.mariaLongitudeRange = -181;
			settings.mariaLatitudeRange = -91;
			settings.mariaCraterCount = 0;
		}
		if(rng.nextBoolean()) {
			settings.secondColorNoise.setIsRidged(false);
		}else {
			settings.secondColorNoise.noiseOffset -= rng.nextDouble() * 0.2;
		}
		settings.secondColorNoise.setNoiseScale(settings.secondColorNoise.noiseLatitudeScale * (1.0 + rng.nextDouble() * 0.9 - 0.45));
		settings.hugeCraterCount = Math.max(0, rng.nextInt(12) - 2);
		settings.smallCraterCount = rng.nextInt(24 * 1024) + 32;
		
		if(rng.nextInt(3) != 0) {
			settings.normalColor = MapUtils.RGB(asteroidColors[rng.nextInt(asteroidColors.length)]);
			settings.mountainsColor = MapUtils.RGB(asteroidColors[rng.nextInt(asteroidColors.length)]);
			if(rng.nextBoolean()) {
				settings.mariasColor = MapUtils.RGB(asteroidColors[rng.nextInt(asteroidColors.length)]);
			}else if(rng.nextInt(3) != 0) {
				double mul = 0.25 + rng.nextDouble() * 0.4;
				settings.mariasColor[0] = settings.normalColor[0] * mul;
				settings.mariasColor[1] = settings.normalColor[1] * mul;
				settings.mariasColor[2] = settings.normalColor[2] * mul;
			}
			if(rng.nextBoolean()) settings.secondaryColor = MapUtils.RGB(asteroidColors[rng.nextInt(asteroidColors.length)]);
			else settings.secondaryColor = settings.normalColor;
		}
		randomnizeColor(settings.normalColor, 0.5, rng);
		randomnizeColor(settings.mountainsColor, 0.5, rng);
		randomnizeColor(settings.mariasColor, 0.5, rng);
		randomnizeColor(settings.secondaryColor, 0.5, rng);
		
		if(rng.nextBoolean()) {
			settings.mountainsNoise.setIsRidged(true);
			settings.craterMountainsNoise.setIsRidged(true);
		}
		settings.mountainsNoise.setNoiseScale(settings.mountainsNoise.noiseLatitudeScale + (rng.nextDouble() * 2.0 - 1.0) * 0.2);
		settings.craterMountainsNoise.setNoiseScale(settings.mountainsNoise.noiseLatitudeScale);
		
		double offset = rng.nextDouble() * 1.5 - 1.0;
		settings.mountainsNoise.noiseStrength += offset;
		settings.craterMountainsNoise.noiseStrength += offset;
		
		settings.mountainsNoise.distortStrength += rng.nextDouble() * 0.4 - 0.2;
		settings.craterMountainsNoise.distortStrength += rng.nextDouble() * 0.8 - 0.4;
		
		if(rng.nextBoolean()) {
			settings.mountainNoise.noiseStrength += rng.nextDouble() * 0.2 - 0.1;
		}else {
			settings.mountainNoise.setNoiseScale(settings.mountainNoise.noiseLatitudeScale + (rng.nextDouble() * 2.0 - 1.0) * 0.4);
		}
		if(rng.nextBoolean()) {
			settings.mountainNoise.setNoiseLongitudeScale(settings.mountainNoise.noiseLatitudeScale * (0.8 + rng.nextDouble() * 0.2));
		}
		settings.mountainNoise.distortStrength += rng.nextDouble() * 0.45 - 0.1;
		
		settings.colorNoise.distortStrength = rng.nextDouble() * 0.58 + 0.4;
		if(rng.nextBoolean()) {
			settings.colorNoise.noise = new OctaveWorley(32, 32, 32, 9 + rng.nextInt(3), 2.0, 0.75 + (rng.nextDouble() * 2.0 - 1.0) * 0.15);
		}
		//Noise latitude and longitude scale should be equal here
		double scale = settings.groundNoiseLargeDetail.noiseLatitudeScale;
		settings.groundNoiseLargeDetail.setNoiseScale(scale + (rng.nextDouble() * 2.0 - 1.0) * 0.4 * scale);
		scale = settings.groundNoiseMediumDetail.noiseLatitudeScale;
		settings.groundNoiseMediumDetail.setNoiseScale(scale + (rng.nextDouble() * 2.0 - 1.0) * 0.4 * scale);
		scale = settings.groundNoiseSmallDetail.noiseLatitudeScale;
		settings.groundNoiseSmallDetail.setNoiseScale(scale + (rng.nextDouble() * 2.0 - 1.0) * 0.4 * scale);
		
		settings.mariaNoise.setNoiseScale(1.5462).setDistortStrength(0.3726);
		if(rng.nextInt(4) != 0) {
			scale = settings.mariaNoise.noiseLatitudeScale;
			settings.mariaNoise.setNoiseScale(scale + (rng.nextDouble() * 2.0 - 1.0) * 0.7 * scale);
			settings.mariaNoise.distortStrength += rng.nextDouble() * 0.7 - 0.1;
			if(settings.mariaNoise.distortStrength > 1.0) settings.mariaNoise.distortStrength = 1.0;
			settings.mariaNoise.noise = new OctaveNoise3D(16, 16, 16, 5 + rng.nextInt(5), 2.0 + (rng.nextDouble() * 2.0 - 1.0) * 0.5, 0.5 + rng.nextDouble() * 0.4);
		}
		
		if(rng.nextBoolean()) {
			settings.groundNoiseLargeDetail.setIsRidged(true);
			settings.groundNoiseLargeDetail.setNoiseOffset(0.1 * rng.nextDouble());
		}
		/*if(rng.nextBoolean()) {
			settings.groundNoiseMediumDetail.setIsRidged(true);
			settings.groundNoiseMediumDetail.setNoiseOffset(0.1);
			settings.groundNoiseSmallDetail.setIsRidged(true);
			settings.groundNoiseSmallDetail.setNoiseOffset(0.05);
		}*/
		
		//double oldMaxsize = settings.craterMaxsize;
		//double oldMinsize = settings.craterMinsize;
		//double oldMariaMaxsize = settings.mariaCraterMaxsize;
		if(rng.nextBoolean()) {
			settings.craterMaxsize = 64 + rng.nextInt(72);
			settings.craterMinsize += rng.nextInt(4) - 2;
			settings.mariaCraterMaxsize = 24 + rng.nextInt(12);
		}
		
		settings.craterMaxsize *= 1.0 / (settings.planetRadius / 200000.0);
		settings.craterMinsize *= 1.0 / (settings.planetRadius / 200000.0);
		
		//double newMinstrength = (settings.craterMinsize - oldMinsize) / (oldMaxsize - oldMinsize) * (settings.craterMaxstrength - settings.craterMinstrength) + settings.craterMinstrength;
		//double newMaxstrength = (settings.craterMaxsize - oldMinsize) / (oldMaxsize - oldMinsize) * (settings.craterMaxstrength - settings.craterMinstrength) + settings.craterMinstrength;
		//settings.craterMinstrength = Math.max(0.5, newMinstrength);
		//settings.craterMaxstrength = newMaxstrength;
		//newMaxstrength = (settings.mariaCraterMaxsize - settings.mariaCraterMinsize) / (oldMariaMaxsize - settings.mariaCraterMinsize) * (settings.mariaCraterMaxstrength - settings.mariaCraterMinstrength) + settings.mariaCraterMinstrength;
		//settings.mariaCraterMaxstrength = newMaxstrength;
		
		if(rng.nextBoolean()) {
			double mul = 1.0 + rng.nextDouble() * 0.3 - 0.15;
			settings.craterRimFades[0] *= mul;
			settings.craterRimFades[1] *= mul;
			settings.craterRimFades[2] *= mul;
			mul = 1.0 + rng.nextDouble() * 0.4 - 0.2;
			settings.mariaCraterRimFades[0] *= mul;
			settings.mariaCraterRimFades[1] *= mul;
			settings.mariaCraterRimFades[2] *= mul;
		}
		
		settings.mariaCraterMaxstrength *= settings.planetRadius / 200000.0;
		settings.mariaCraterMinstrength *= settings.planetRadius / 200000.0;
		
		return settings;
	}
	
	private AsteroidMoonGen.AsteroidGenSettings randomAsteroidSettings(long seed, int radius/*double randomnizationStrength*/){
		AsteroidMoonGen.AsteroidGenSettings settings = new AsteroidMoonGen.AsteroidGenSettings();
		Random rng = new Random(seed);
		settings.planetRadius = 20000;
		settings.width = 8192;
		settings.height = 4096;
		double craterStrengthMul = (double)settings.planetRadius / 20000.0;
		if(craterStrengthMul > 1) craterStrengthMul = 1;
		settings.craterMaxstrength *= craterStrengthMul;
		settings.craterMinstrength *= craterStrengthMul;
		
		settings.normalColor = MapUtils.RGB(asteroidColors[rng.nextInt(asteroidColors.length)]);
		settings.peaksColor = MapUtils.RGB(asteroidColors[rng.nextInt(asteroidColors.length)]);
		if(rng.nextBoolean()) settings.secondaryColor = MapUtils.RGB(asteroidColors[rng.nextInt(asteroidColors.length)]);
		else settings.secondaryColor = settings.normalColor;
		
		if(rng.nextBoolean()) randomnizeColor(settings.normalColor, 0.5, rng);
		if(rng.nextBoolean()) randomnizeColor(settings.peaksColor, 0.5, rng);
		if(rng.nextBoolean()) randomnizeColor(settings.secondaryColor, 0.5, rng);
		
		if(rng.nextBoolean()) {
			settings.ridgedShape = true;
			settings.shapeNoise.setIsRidged(true);
			settings.shapeNoise.setNoiseOffset(0.078);
		}else if(rng.nextBoolean()) {
			settings.shapeNoise.noiseOffset += (rng.nextDouble() * 2.0 - 1.0) * 0.1;
		}
		settings.shapeNoise.setNoiseScale(1.5 + rng.nextDouble() * 1.0);
		settings.shapeNoise.noiseStrength += (rng.nextDouble() * 2.0 - 1.0) * 0.35;
		settings.shapeNoise.noise = new OctaveNoise3D(16, 16, 16, 3 + rng.nextInt(2), 2.0 + rng.nextDouble() - 0.5, 0.32 + rng.nextDouble() * 0.4 - 0.1);
		if(rng.nextBoolean()) {
			settings.shapeNoise.setNoiseLatitudeScale(settings.shapeNoise.noiseLongitudeScale * (0.8 + 0.2 * rng.nextDouble()));
		}
		
		if(rng.nextBoolean()) {
			settings.groundNoise.setIsRidged(true);
			settings.groundNoise.setNoiseOffset(rng.nextDouble() * 0.1);
		}else {
			settings.groundNoise.noiseOffset -= rng.nextDouble() * 0.15;
		}
		if(rng.nextBoolean()) {
			settings.secondColorNoise.setIsRidged(true);
			settings.secondColorNoise.setNoiseOffset(0);
		}
		
		double scale = settings.secondColorNoise.noiseLatitudeScale;
		settings.secondColorNoise.setNoiseScale(scale + (rng.nextDouble() * 2.0 - 1.0) * 0.4 * scale);
		scale = settings.groundNoise.noiseLatitudeScale;
		settings.groundNoise.setNoiseScale(scale + (rng.nextDouble() * 2.0 - 1.0) * 0.25 * scale);
		scale = settings.peakNoise.noiseLatitudeScale;
		settings.peakNoise.setNoiseScale(scale + (rng.nextDouble() * 2.0 - 1.0) * 0.25 * scale);
		
		if(rng.nextBoolean()) {
			settings.secondColorNoise.noise = new OctaveNoise3D(16, 16, 16, 2 + rng.nextInt(2), 2.0 + (rng.nextDouble() * 0.2 - 0.1), 0.5 + rng.nextDouble() * 0.1);
		}
		
		if(rng.nextBoolean()) {
			settings.craterMaxsize = 96 + rng.nextInt(48);
			settings.craterMinsize += rng.nextInt(4) - 2;
		}
		settings.craterCount = 24 + rng.nextInt(768);
		
		return settings;
	}
	
	private static double[] randomnizeColor(double[] col, double amount, Random rng) {
		double u = rng.nextDouble();
		col[0] += (u * 2.0 - 1.0) * amount * col[0];
		col[1] += (u * 2.0 - 1.0) * amount * col[1];
		col[2] += (u * 2.0 - 1.0) * amount * col[2];
		col[0] = Math.min(1, Math.max(0, col[0]));
		col[1] = Math.min(1, Math.max(0, col[1]));
		col[2] = Math.min(1, Math.max(0, col[2]));
		return col;
	}
	
	private static Color[] asteroidColors = new Color[] {
		new Color(58, 54, 56),
		new Color(159, 159, 159),
		new Color(223, 219, 210),
		new Color(192,192,192),
		new Color(112,106,106),
		new Color(158,171,170),
		new Color(64, 82, 83),
		new Color(76, 61, 61),
		new Color(83, 64, 64),
		new Color(146, 104, 88),
		new Color(146, 104, 88),
		new Color(78, 59, 44),
		new Color(115, 46, 26),
		new Color(115, 46, 26),
		new Color(108, 101, 90),
		new Color(116, 79, 73),
		new Color(228, 219, 230),
		new Color(60, 42, 22),
		new Color(50, 41, 32),
		new Color(217, 204, 185),
		new Color(230, 207, 175),
		new Color(152, 179, 184),
	};
	
}
