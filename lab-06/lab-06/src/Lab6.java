import java.lang.String;
import java.util.*;
import java.io.IOException;

import bridges.base.Circle;
import bridges.base.GraphAdjList;
import bridges.base.SymbolCollection;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.validation.RateLimitException;
import bridges.data_src_dependent.City;

public class Lab6 {

	public static void main(String[] args) throws IOException, RateLimitException {

		Bridges bridges = new Bridges(17, "amanw", System.getenv("API_KEY"));

		DataSource ds = bridges.getDataSource();
		SymbolCollection sc = new SymbolCollection();

		// Already defined in the location properties
		double minX = -1242.1788330078125;
		double minY = 245.55239868164062;
		double maxX = -687.778076171875;
		double maxY = 489.4649963378906;

		sc.setViewport((float) minX, (float) maxX, (float) minY, (float) maxY);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("min_pop", "10000");

		// now get the data
		Vector<City> cities = ds.getUSCitiesData(params);

		bridges.setTitle("Quad Tree");

		bridges.setDescription("Quad Tree");
		Point topLeft = new Point((float) minX, (float) maxY);
		Point botRight = new Point((float) maxX, (float) minY);

		Boundary bd = new Boundary(topLeft, botRight, sc);

		QuadTree quadTree = new QuadTree(bd, sc);

		for (City city : cities) {
			if (!city.getState().equals("AK") && !city.getState().equals("HI")) {
				Circle cityCircle = new Circle(city.getLongitude() * 10, city.getLatitude() * 10, 2.0f);
				cityCircle.setFillColor("red");
				sc.addSymbol(cityCircle);

				Point point = new Point((float) city.getLongitude() * 10, (float) city.getLatitude() * 10);
				String pointName = city.getCity() + "_" + city.getState();

				Node node = new Node(point, pointName);
				quadTree.insert(node);
			}
		}

		bridges.setDataStructure(sc);
		bridges.visualize();

		Point charlotte = quadTree.find("Charlotte_NC");
		Point centralia = quadTree.find("Centralia_WA");
		Point losAngeles = quadTree.find("Los Angeles_CA");

		quadTree.query(charlotte, 0, "Charlotte_NC");
		quadTree.query(centralia, 0, "Centralia_WA");
		quadTree.query(losAngeles, 0, "Los Angeles_CA");

		bridges.visualize();

	}

}
