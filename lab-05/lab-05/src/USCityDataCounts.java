import java.lang.String;
import java.util.*;
import java.io.IOException;
import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.base.Edge;
import bridges.validation.RateLimitException;
import bridges.data_src_dependent.City;

public class USCityDataCounts {

    public static void main(String[] args) throws IOException, RateLimitException {

        Bridges bridges = new Bridges(17, "amanw", System.getenv("API_KEY"));

        bridges.setTitle("MST On Test Graph");

        bridges.setDescription("Below is the test graph.");

        bridges.setCoordSystemType("albersusa");
        bridges.setMapOverlay(true);

        DataSource ds = bridges.getDataSource();

        int populationThreshold = 20_000;

        while (populationThreshold <= 640_000) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("min_pop", String.valueOf(populationThreshold));

            Vector<City> cities = ds.getUSCitiesData(params);

            GraphAdjList<String, String, Double> newGraph = new GraphAdjList<>();

            for (City city : cities) {
                String cityName = city.getCity();
                newGraph.addVertex(cityName, cityName);
                newGraph.getVertex(cityName).setLocation(city.getLongitude(), city.getLatitude());
            }
            HashMap<String, HashMap<String, Double>> hashGraph = weightedGraph(cities);

            System.out.println("Test graph created.");

            MSTResult totalCost = primMST(hashGraph, "Charlotte_NC", newGraph);

            int totalVertices = totalCost.totalEdges + 1;

            bridges.setTitle(
                    "MST (US Cities): Population Threshold: " + populationThreshold + ", Vertices: " + totalVertices
                            + ", Edges: " + totalCost.totalEdges + ", Cost: " + Math.round(totalCost.totalCost));

            bridges.setDataStructure(newGraph);
            bridges.visualize();

            populationThreshold *= 2;
        }
    }

    static HashMap<String, HashMap<String, Double>> weightedGraph(Vector<City> cities) {
        HashMap<String, HashMap<String, Double>> hashGraph = new HashMap<>();

        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                City city1 = cities.get(i);
                City city2 = cities.get(j);
                double distance = getDist(city1.getLatitude(), city1.getLongitude(), city2.getLatitude(),
                        city2.getLongitude());

                hashGraph.putIfAbsent(city1.getCity(), new HashMap<>());
                hashGraph.putIfAbsent(city2.getCity(), new HashMap<>());

                hashGraph.get(city1.getCity()).put(city2.getCity(), distance);
                hashGraph.get(city2.getCity()).put(city1.getCity(), distance);
            }
        }
        return hashGraph;
    }

    static class MSTResult {
        double totalCost;
        int totalEdges;

        public MSTResult(double totalCost, int totalEdges) {
            this.totalCost = totalCost;
            this.totalEdges = totalEdges;
        }
    }

    static MSTResult primMST(HashMap<String, HashMap<String, Double>> G, String s,
            GraphAdjList<String, String, Double> newGraph) {

        HashSet<String> mark = new HashSet<>();
        HashMap<String, Double> minDist = new HashMap<>();
        HashMap<String, String> treeEdges = new HashMap<>();

        for (String v : G.keySet()) {
            minDist.put(v, Double.POSITIVE_INFINITY);
        }

        minDist.put(s, 0.0);

        for (int i = 0; i < G.size(); i++) {
            String minVert = null;
            for (String v : G.keySet()) {
                if (!mark.contains(v) && (minVert == null || minDist.get(v) < minDist.get(minVert))) {
                    minVert = v;
                }
            }

            mark.add(minVert);

            for (String w : G.get(minVert).keySet()) {
                double weight = G.get(minVert).get(w);
                if (!mark.contains(w) && weight < minDist.get(w)) {
                    minDist.put(w, weight);
                    treeEdges.put(w, minVert);
                }
            }
        }

        double totalCost = 0;
        int totalEdges = 0;
        for (Map.Entry<String, String> entry : treeEdges.entrySet()) {
            String to = entry.getKey();
            String from = entry.getValue();
            double weight = G.get(from).get(to);

            newGraph.addEdge(from, to, weight);
            newGraph.getLinkVisualizer(from, to).setLabel(String.valueOf(weight));
            newGraph.getLinkVisualizer(from, to).setColor("red");
            newGraph.getLinkVisualizer(from, to).setThickness(1.5);
            newGraph.getVertex(from).setColor("red");
            newGraph.getVertex(to).setColor("red");

            totalEdges++;
            totalCost += weight;
        }
        totalCost /= 1000;
        return new MSTResult(totalCost, totalEdges);
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
