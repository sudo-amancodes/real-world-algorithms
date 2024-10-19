import java.lang.String;
import java.util.*;
import java.io.IOException;
import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.base.Edge;
import bridges.validation.RateLimitException;
import bridges.data_src_dependent.City;

public class Task1 {

	public static void main(String[] args) throws IOException, RateLimitException {

		Bridges bridges = new Bridges(14, "amanw", System.getenv("API_KEY"));

		bridges.setTitle("MST On Test Graph");

		bridges.setDescription("Below is the test graph.");

		// uncomment the next 2 lines if you are building graphs on the
		// dataset with lat/long positions on the US map

		// bridges.setCoordSystemType("albersusa");
		// bridges.setMapOverlay(true);

		// Illustrates how to get US cities with population larger than 100000
		DataSource ds = bridges.getDataSource();

		// set the parameters
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("state", "NC");
		params.put("min_pop", "100000");

		// now get the data
		Vector<City> cities = ds.getUSCitiesData(params);

		// print the first two cities and the distance between them
		System.out.println("First 2 cities are " +
				cities.get(0).getCity() + "," + cities.get(0).getState() + " and " +
				cities.get(1).getCity() + "," + cities.get(1).getState());

		System.out.println("Dist. between the two cities: " +
				getDist(cities.get(0).getLatitude(), cities.get(0).getLongitude(),
						cities.get(1).getLatitude(), cities.get(1).getLongitude())
				+ " meters");

		// build the test graph

		GraphAdjList<String, String, Double> graph = new GraphAdjList<>();
		createTestGraph(graph);
		System.out.println("Test graph created.");

		bridges.setDataStructure(graph);
		bridges.visualize();

		GraphAdjList<String, String, Double> newGraph = new GraphAdjList<>();

		newGraph.addVertex("a", "a");
		newGraph.addVertex("b", "b");
		newGraph.addVertex("c", "c");
		newGraph.addVertex("d", "d");
		newGraph.addVertex("e", "e");
		newGraph.addVertex("f", "f");

		int tx = 400, ty = -200;

		newGraph.getVertex("a").setLocation(200 + tx, 0 + ty);
		newGraph.getVertex("d").setLocation(600 + tx, 0 + ty);
		newGraph.getVertex("f").setLocation(400 + tx, 0 + ty);

		newGraph.getVertex("b").setLocation(300 + tx, 100 + ty);
		newGraph.getVertex("c").setLocation(500 + tx, 100 + ty);
		newGraph.getVertex("e").setLocation(400 + tx, -100 + ty);

		String startVertex = "a";
		bridges.setTitle("Total cost of the MST: " + primMST(graph, startVertex, newGraph));

		bridges.setDescription("Below is the primMST total test graph.");

		bridges.setDataStructure(newGraph);
		bridges.visualize();

	}

	static double primMST(GraphAdjList<String, String, Double> graph, String start,
			GraphAdjList<String, String, Double> newGraph) {

		HashSet<String> mark = new HashSet<>();
		HashMap<String, Double> minDist = new HashMap<>();
		HashMap<String, String> treeEdges = new HashMap<>();

		for (String vertex : graph.getVertices().keySet()) {
			minDist.put(vertex, Double.POSITIVE_INFINITY);
		}

		minDist.put(start, 0.0);

		for (int i = 0; i < graph.getVertices().size(); i++) {
			String minVert = null;
			for (String vertex : graph.getVertices().keySet()) {
				if (!mark.contains(vertex) && (minVert == null || minDist.get(vertex) < minDist.get(minVert))) {
					minVert = vertex;
				}
			}

			mark.add(minVert);

			for (Edge<String, Double> edge : graph.outgoingEdgeSetOf(minVert)) {
				String w = edge.getTo();
				double weight = edge.getEdgeData();
				if (!mark.contains(w) && weight < minDist.get(w)) {
					minDist.put(w, weight);
					treeEdges.put(w, minVert);
				}
			}
		}

		double totalCost = 0;
		for (Map.Entry<String, String> entry : treeEdges.entrySet()) {
			String from = entry.getValue();
			String to = entry.getKey();
			double weight = graph.getEdgeData(from, to);

			newGraph.addEdge(to, from, weight);
			newGraph.getLinkVisualizer(to, from).setLabel(String.valueOf(weight));

			newGraph.getLinkVisualizer(to, from).setColor("red");
			newGraph.getLinkVisualizer(to, from).setThickness(1.5);
			newGraph.getVertex(to).setColor("red");
			newGraph.getVertex(from).setColor("red");

			System.out.println("Edge: " + from + " - " + to + " Weight: " + weight);
			totalCost += weight;
		}
		return totalCost;
	}

	static void createTestGraph(GraphAdjList<String, String, Double> gr) {
		gr.addVertex("a", "a");
		gr.addVertex("b", "b");
		gr.addVertex("c", "c");
		gr.addVertex("d", "d");
		gr.addVertex("e", "e");
		gr.addVertex("f", "f");

		// set locations
		int tx = 400, ty = -200;

		gr.getVertex("a").setLocation(200 + tx, 0 + ty);
		gr.getVertex("d").setLocation(600 + tx, 0 + ty);
		gr.getVertex("f").setLocation(400 + tx, 0 + ty);

		gr.getVertex("b").setLocation(300 + tx, 100 + ty);
		gr.getVertex("c").setLocation(500 + tx, 100 + ty);
		gr.getVertex("e").setLocation(400 + tx, -100 + ty);

		// add edges
		gr.addEdge("a", "b", 3.);
		gr.addEdge("b", "a", 3.);

		gr.addEdge("a", "e", 6.);
		gr.addEdge("e", "a", 6.);

		gr.addEdge("a", "f", 1.);
		gr.addEdge("f", "a", 1.);

		gr.addEdge("b", "c", 1.);
		gr.addEdge("c", "b", 1.);

		gr.addEdge("b", "f", 4.);
		gr.addEdge("f", "b", 4.);

		gr.addEdge("c", "d", 6.);
		gr.addEdge("d", "c", 6.);

		gr.addEdge("c", "f", 4.);
		gr.addEdge("f", "c", 4.);

		gr.addEdge("d", "e", 1.);
		gr.addEdge("e", "d", 1.);

		gr.addEdge("d", "f", 1.);
		gr.addEdge("f", "d", 1.);

		gr.addEdge("e", "f", 2.);
		gr.addEdge("f", "e", 2.);

		// set edge labels
		for (String v : gr.getVertices().keySet()) {
			for (Edge<String, Double> edge : gr.outgoingEdgeSetOf(v)) {
				// set edge thickness
				String src = edge.getFrom(), dest = edge.getTo();
				gr.getLinkVisualizer(src, dest).setThickness(1.5);
				// set edge label
				String l = String.valueOf(gr.getEdgeData(src, dest));
				gr.getLinkVisualizer(src, dest).setLabel(l);
			}
		}
	}

	static double getDist(double lat1, double long1, double lat2, double long2) {
		// uses the haversine formula
		final int R = 6371000; // meters
		final double phi1 = Math.toRadians(lat1);
		final double phi2 = Math.toRadians(lat2);
		final double delPhi = Math.toRadians((lat2 - lat1));
		final double delLambda = Math.toRadians((long2 - long1));

		final double a = Math.sin(delPhi / 2) * Math.sin(delPhi / 2)
				+ Math.cos(phi1) * Math.cos(phi2)
						* Math.sin(delLambda / 2) * Math.sin(delLambda / 2);
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c; // meters
	}
}
