package rms.alert.utils.geoboundary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import rms.alert.utils.geoboundary.beans.Boundary;
import rms.alert.utils.geoboundary.beans.MyPoint;

//Ref 1: https://www.igismap.com/vietnam-shapefile-download-country-boundaryline-polygon/
//Ref 2: https://mapshaper.org/
//Note: We can convert shapefile (Ref 1) to geoJSON by (Ref 2)
//Formula for detecting one point is in polygon or not: https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon

@Component
public class GeoBoundary {

	private static final Logger logger = LogManager.getLogger();
	private List<Boundary> geoBoundary = new ArrayList<>();

	private void findMinMaxBoundaryList() {
		for (Boundary boun : geoBoundary) {
			boun.findMinMaxBoundary();
		}
	}

	@Autowired
	private Environment environment;

	private String getConfigPath() {
		try {
			return environment.getProperty("app.path").trim();
		} catch (Exception e) {
			return ".";
		}
	}

	private static String readLineByLineJava8(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			logger.error("Read vngeo file fail: {}", e.getMessage());
		}
		return contentBuilder.toString();
	}

	@PostConstruct
	private void loadBoundary() {
		try {
			JSONObject jo = new JSONObject(readLineByLineJava8(getConfigPath() + "/conf/vnboundary/vngeo.json"));
			JSONArray jaMultiCoors = jo.getJSONArray("MultiCoors");
			for (int i = 0; i < jaMultiCoors.length(); ++i) {
				JSONArray jaCoors = (JSONArray) jaMultiCoors.get(i);
				Boundary boun = new Boundary();
				for (int j = 0; j < jaCoors.length(); ++j) {
					String coor = jaCoors.get(j).toString();
					coor = coor.substring(1, coor.length() - 1);
					String[] stringMyPoint = coor.split(",");
					MyPoint p = new MyPoint(Double.parseDouble(stringMyPoint[0]), Double.parseDouble(stringMyPoint[1]));
					boun.addMyPoint(p);
				}
				geoBoundary.add(boun);
			}

			findMinMaxBoundaryList();
			logger.info("Read file \"vnboundary/vngeo.json\" and process info successfully");
		} catch (Exception e) {
			logger.error("Read file \"vnboundary/vngeo.json\" fail");
		}
	}

	private boolean IsPointInBoundary(MyPoint mp, Boundary boun) {
		List<MyPoint> mplist = boun.getBoundary();

		double minX = boun.getMinX();
		double maxX = boun.getMaxX();
		double minY = boun.getMinY();
		double maxY = boun.getMaxY();
		if (mp.getX() < minX || mp.getX() > maxX || mp.getY() < minY || mp.getY() > maxY) {
			return false;
		}

		boolean inside = false;
		int i;
		int j;
		for (i = 0, j = mplist.size() - 1; i < mplist.size(); j = i++) {
			if ((mplist.get(i).getY() > mp.getY()) != (mplist.get(j).getY() > mp.getY())
					&& mp.getX() < ((mplist.get(j).getX() - mplist.get(i).getX()) * (mp.getY() - mplist.get(i).getY())
							/ (mplist.get(j).getY() - mplist.get(i).getY()) + mplist.get(i).getX())) {
				inside = !inside;
			}
		}

		return inside;
	}

	public boolean IsPointInBoundaryList(MyPoint mp) {
		boolean isIn = false;
		for (Boundary boun : geoBoundary) {
			boolean result = IsPointInBoundary(mp, boun);
			isIn = isIn | result;
		}
		return isIn;
	}

}
