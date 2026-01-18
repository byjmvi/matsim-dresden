package org.matsim.location;

import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.gis.ShapeFileReader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CoordInFeatureCkecker {

	private final String shapeFile;
	private final Map<String, Geometry> featureMap = new HashMap<>();

	GeometryFactory geometryFactory = new GeometryFactory();


	public static void main(String[] args) {
		String shapeFile = "input/shp/gem_25832";

		String networkFile = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/dresden/dresden-v1.0/input/dresden-v1.0-network-with-pt.xml.gz";

		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		Network network = scenario.getNetwork();

		MatsimNetworkReader networkReader = new MatsimNetworkReader(network);
		networkReader.readFile(networkFile);

		CoordInFeatureCkecker coordInFeatureCkecker = new CoordInFeatureCkecker(shapeFile);

		Id<Link> linkId = Id.createLinkId("572364039"); //Hettnerstraße
		Link link = network.getLinks().get(linkId);

		String featureId = "14612000";
		boolean answer = coordInFeatureCkecker.checkIfLinkInFeature(link, featureId);

		System.out.println("Is link with ID " + linkId + " within feature with ID " + featureId + "?" + answer);
	}




	public CoordInFeatureCkecker(String shapeFile) {
		this.shapeFile = shapeFile;

		Collection<SimpleFeature> allFeatures = ShapeFileReader.getAllFeatures(shapeFile);

		for (SimpleFeature feature : allFeatures) {
			String schluessel = feature.getAttribute("SCHLUESSEL").toString();
			Geometry geometry = (Geometry) feature.getDefaultGeometry();

			featureMap.put(schluessel, geometry);
		}

	}


	public boolean checkIfLinkInFeature(Link link, String feautureId) {
		Geometry geometry = featureMap.get(feautureId);

		Coord linkCenter = link.getCoord();
		Coordinate coordinate = new Coordinate(linkCenter.getX(), linkCenter.getY());
		Point point = geometryFactory.createPoint(coordinate);

		boolean isFeatureContainsPoint = false;
		if (geometry.contains(point)) {
			isFeatureContainsPoint = true;
		}

		return isFeatureContainsPoint;
	}

}
