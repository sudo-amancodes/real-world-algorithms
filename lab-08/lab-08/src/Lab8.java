import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import bridges.connect.Bridges;
import bridges.base.Array;
import bridges.base.Color;
import bridges.base.GraphAdjList;

public class Lab8 {

    public static void main(String[] args) throws Exception {

        // initialize BRIDGES, set credentials
        Bridges bridges = new Bridges(19, "amanw", System.getenv("API_KEY"));
        // set title
        bridges.setTitle("Lab 8: Floyd's All Pairs Shortest Paths - US Cities");

        // set description
        bridges.setDescription("Lab 8: Floyd's All Pairs Shortest Paths - US Cities");

        // Set the visualization settings
        bridges.setCoordSystemType("albersusa");
        bridges.setMapOverlay(true);
        bridges.setMap("us", "all");

        GraphAdjList<String, String, Double> graph = new GraphAdjList<>();

        File file = new File("/Users/aman/Documents/ITCS-4114-Real-World-Algo/lab-08/lab-08/graph.txt");

        List<String> nameList = new ArrayList<>();

        try (Scanner sc = new Scanner(file)) {
            // Read the vertices count
            String line = sc.nextLine();
            String[] splitVertices = line.split(" ");
            double numVertices = Double.parseDouble(splitVertices[1]);

            for (int i = 0; i < (int) numVertices; i++) {
                line = sc.nextLine();
                String[] parts = line.split("\\s+");
                String name = parts[1];
                double lat = Double.parseDouble(parts[2]);
                double longi = Double.parseDouble(parts[3]);
                graph.addVertex(name, name);
                graph.getVertex(name).setLocation(longi, lat);
                nameList.add(name);
            }

            // Read the edges count
            line = sc.nextLine();
            String[] splitEdges = line.split(" ");
            double numEdges = Double.parseDouble(splitEdges[1]);

            for (int i = 0; i < (int) numEdges; i++) {
                line = sc.nextLine();
                String[] parts = line.split("\\s+");
                String city1 = parts[1];
                String city2 = parts[2];
                double distance = Double.parseDouble(parts[3]);
                graph.addEdge(city1, city2, distance);
                graph.addEdge(city2, city1, distance);
            }
        }

        List<Object> myMatrixPath = floydsAlgorithm(graph, nameList);

        Double[][] matrix = (Double[][]) myMatrixPath.get(0);
        String[][] path = (String[][]) myMatrixPath.get(1);

        bridges.setDataStructure(graph);
        bridges.visualize();

        findPath("Charlotte_NC", "Los_Angeles_CA", nameList, matrix, path, graph);
        findPath("Charlotte_NC", "Seattle_WA", nameList, matrix, path, graph);
        findPath("Charlotte_NC", "Houston_TX", nameList, matrix, path, graph);
        findPath("Charlotte_NC", "Burlington_VT", nameList, matrix, path, graph);

        bridges.setDataStructure(graph);
        bridges.visualize();
    }

    private static void findPath(String start, String end, List<String> nameList, Double[][] matrix, String[][] path,
            GraphAdjList<String, String, Double> graph) {
        int a = nameList.indexOf(start);
        int b = nameList.indexOf(end);
        int distance = (int) Math.round(matrix[a][b]);

        graph.getVertex(nameList.get(b)).setColor("green");
        graph.getVertex(nameList.get(b)).setLabel(start + " to " + end + ":" + distance);
        if (path[a][b] == null) {
            System.out.println("There is no path from A to B");
        } else {
            String pathStr = end;
            while (b != a) {
                int temp = b;
                b = nameList.indexOf(path[a][b]);
                pathStr = nameList.get(b) + " -> " + pathStr;
                graph.getLinkVisualizer(nameList.get(b), nameList.get(temp)).setColor("red");

                if (!graph.getVertex(nameList.get(b)).getColor().equals(new Color("green"))) {
                    graph.getVertex(nameList.get(b)).setColor("red");
                }
                if (!graph.getVertex(nameList.get(temp)).getColor().equals(new Color("green"))) {
                    graph.getVertex(nameList.get(temp)).setColor("red");
                }
            }
            System.out.println("The path from A to B is: " + pathStr);

        }
    }

    private static List<Object> floydsAlgorithm(GraphAdjList<String, String, Double> graph, List<String> nameList) {
        Double[][] adjMatrix = getAdjacencyMatrix(graph, nameList);
        String[][] path = new String[adjMatrix.length][adjMatrix.length];

        // Initialize path array
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = 0; j < adjMatrix.length; j++) {
                if (adjMatrix[i][j] != Double.MAX_VALUE) {
                    path[i][j] = nameList.get(i);
                } else {
                    path[i][j] = null;
                }
            }
        }

        for (int k = 0; k < adjMatrix.length; k++) {
            for (int i = 0; i < adjMatrix.length; i++) {
                for (int j = 0; j < adjMatrix.length; j++) {
                    if (adjMatrix[i][k] + adjMatrix[k][j] < adjMatrix[i][j]) {
                        adjMatrix[i][j] = adjMatrix[i][k] + adjMatrix[k][j];
                        path[i][j] = path[k][j];
                    }
                }
            }
        }

        List<Object> result = new ArrayList<>();
        result.add(adjMatrix);
        result.add(path);
        return result;

    }

    // Helper method to get the adjacency matrix from the graph
    private static Double[][] getAdjacencyMatrix(GraphAdjList<String, String, Double> graph, List<String> nameList) {
        Double[][] adjMatrix = new Double[graph.getVertices().size()][graph.getVertices().size()];

        for (int i = 0; i < graph.getVertices().size(); i++) {
            for (int j = 0; j < graph.getVertices().size(); j++) {
                if (i == j) {
                    adjMatrix[i][j] = 0.0;
                } else {
                    adjMatrix[i][j] = Double.MAX_VALUE;
                }
            }
        }

        for (int i = 0; i < graph.getVertices().size(); i++) {
            String source = nameList.get(i);

            for (int j = 0; j < graph.getVertices().size(); j++) {
                String destination = nameList.get(j);
                if (graph.getEdgeData(source, destination) != null) {
                    Double weight = graph.getEdgeData(source, destination);
                    adjMatrix[i][j] = weight;
                }
            }
        }

        return adjMatrix;
    }

}
