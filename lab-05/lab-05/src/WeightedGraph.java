import java.lang.String;
import java.util.*;
import java.io.IOException;
import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.base.Edge;
import bridges.validation.RateLimitException;
import bridges.data_src_dependent.City;

public class WeightedGraph {

    public static void main(String[] args) throws IOException, RateLimitException {

        Bridges bridges = new Bridges(15, "amanw", System.getenv("API_KEY"));

        bridges.setTitle("MST On Test Graph");

        bridges.setDescription("Below is the test graph.");

        // uncomment the next 2 lines if you are building graphs on the
        // dataset with lat/long positions on the US map

        bridges.setCoordSystemType("albersusa");
        // bridges.setMapOverlay(true);

        // Illustrates how to get US cities with population larger than 100000
        DataSource ds = bridges.getDataSource();

        // set the parameters
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("min_pop", "100000");

        // now get the data
        Vector<City> cities = ds.getUSCitiesData(params);

        GraphAdjList<String, String, Double> graph = new GraphAdjList<>();
        // GraphAdjList<String, String, Double> newGraph = new GraphAdjList<>();
        for (City city : cities) {
            String cityName = city.getCity();
            graph.addVertex(cityName, cityName);
            graph.getVertex(cityName).setLocation(city.getLongitude(), city.getLatitude());
        }
        weightedGraph(cities, graph);

        System.out.println("Test graph created.");

        bridges.setDataStructure(graph);
        bridges.visualize();

    }

    static void weightedGraph(Vector<City> cities, GraphAdjList<String, String, Double> graph) {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                City city1 = cities.get(i);
                City city2 = cities.get(j);
                double distance = getDist(city1.getLatitude(), city1.getLongitude(), city2.getLatitude(),
                        city2.getLongitude());
                graph.addEdge(city1.getCity(), city2.getCity(), distance);
                graph.addEdge(city2.getCity(), city1.getCity(), distance);

                String l = String.valueOf(distance);
                graph.getLinkVisualizer(city1.getCity(), city2.getCity()).setLabel(l);
            }
        }
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

    public static void dfsTraversal(GraphAdjList<String, String, Double> graph, String start,
            HashSet<String> visited, Vector<String> tour) {
        visited.add(start);
        tour.add(start);
        for (Edge<String, Double> edge : graph.outgoingEdgeSetOf(start)) {
            String neighbor = edge.getTo();
            if (!visited.contains(neighbor)) {
                dfsTraversal(graph, neighbor, visited, tour);
            }
        }
    }

    // Function to remove duplicate vertices from the tour
    public static Vector<String> removeDuplicates(Vector<String> tour) {
        HashSet<String> seen = new HashSet<>();
        Vector<String> uniqueTour = new Vector<>();
        for (String city : tour) {
            if (!seen.contains(city)) {
                seen.add(city);
                uniqueTour.add(city);
            }
        }
        return uniqueTour;
    }
}
