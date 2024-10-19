import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import bridges.base.Color;
import bridges.base.Edge;
import bridges.base.GraphAdjListSimple;
import bridges.base.SLelement;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.ActorMovieIMDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

// A test class that fetches a random movie from IMDB then sends the
//	actors name and movie title + release date to the console as output.
//	There is no visual output to the users BRIDGES gallery after running this code.

public class App {
    private static Set<String> notVisited = new HashSet<>();

    private static Map<String, String> parentMap = new HashMap<>();

    private static HashSet<String> isActor = new HashSet<>();

    public static void bfs(GraphAdjListSimple<String> graph, HashMap<String, ArrayList<String>> actorAndMovie,
            String current) {

        Queue<String> queue = new LinkedList<>();

        notVisited.remove(current);

        queue.add(current);

        int currentColor = 0;

        int count = 1;

        isActor.remove(current);
        isActor.add(current + "(" + currentColor + ")");

        while (!queue.isEmpty()) {

            String popString = queue.remove();

            count--;

            graph.getVertex(popString).setLabel(popString + "(" + currentColor + ")");

            for (String nextVertex : actorAndMovie.get(popString)) {
                if (notVisited.contains(nextVertex)) {

                    notVisited.remove(nextVertex);
                    queue.add(nextVertex);

                    graph.addEdge(popString, nextVertex);
                    graph.addEdge(nextVertex, popString);

                    graph.getVertex(nextVertex).setLabel(nextVertex + "(" + currentColor + ")");
                    parentMap.put(nextVertex, popString);
                }

            }
            if (isActor.contains(popString)) {
                isActor.remove(popString);
                isActor.add(popString + "(" + currentColor + ")");
            }
            if (count == 0) {
                if (currentColor <= 8) {
                    currentColor++;
                }

                count = queue.size();

            }

        }

    }

    public static void colorPath(GraphAdjListSimple<String> graph, Map<String, String> parentMap, String start) {
        ArrayList<String> allValues = new ArrayList<>();

        for (String actor : isActor) {
            if (actor.matches(".*\\((5|6|7|8|9)\\)$")) {
                allValues.add(actor.substring(0, actor.length() - 3));
            }
        }
        for (String actor : allValues) {
            String current = actor;

            while (current != null && parentMap.get(current) != null) {
                graph.getLinkVisualizer(current, parentMap.get(current)).setColor("orange");
                graph.getLinkVisualizer(current, parentMap.get(current)).setThickness(5.0f);

                graph.getVertex(current).setColor("red");
                graph.getVertex(current).setSize(20);

                graph.getVertex(parentMap.get(current)).setColor("red");
                graph.getVertex(parentMap.get(current)).setSize(10);

                current = parentMap.get(current);

            }
        }
        graph.getVertex(start).setColor("cyan");
        graph.getVertex(start).setSize(35);
    }

    public static void printPath(Map<String, String> parentMap, String start, String end) {
        String current = end;
        int distance = -1;
        // System.out.println(parentMap.get(current));
        while (current != null) {
            System.out.print(current + " -> ");
            current = parentMap.get(current);
            distance++;
        }
        System.out.println("head");
        System.out.println("Distance: " + distance);
    }

    public static void main(String[] args) throws Exception {

        // create the Bridges object
        Bridges bridges = new Bridges(13, "amanw", System.getenv("API_KEY"));

        DataSource ds = bridges.getDataSource();

        // set a title
        bridges.setTitle("A Simple Adjacency list based Graph Example.");

        // set description
        bridges.setDescription("Demonstrate how to create a graph with a few nodes and display it");

        // create an adjacency list based graph
        GraphAdjListSimple<String> graph = new GraphAdjListSimple<String>();

        // Get a List of ActorMovieIMDB objects from Bridges
        List<ActorMovieIMDB> mylist = ds.getActorMovieIMDBData(1813);

        // Inspect a random ActorMovieIMDB object

        HashMap<String, ArrayList<String>> actorAndMovie = new HashMap<>();

        // Iterate through the ActorMovieIMDB objects
        for (ActorMovieIMDB pair : mylist) {
            String actor = pair.getActor();
            String movie = pair.getMovie();

            if (!actorAndMovie.containsKey(actor)) {
                graph.addVertex(actor, actor);
                isActor.add(actor);
            }
            if (!actorAndMovie.containsKey(movie)) {
                graph.addVertex(movie, movie);
            }

            if (!actorAndMovie.containsKey(actor)) {
                // If not, add the actor node to the graph with an empty adjacency list
                actorAndMovie.put(actor, new ArrayList<>());

                actorAndMovie.get(actor).add(movie);

            } else if (actorAndMovie.containsKey(actor) && !actorAndMovie.get(actor).contains(movie)) {
                actorAndMovie.get(actor).add(movie);
            }

            // Check if the movie node already exists in the graph
            if (!actorAndMovie.containsKey(movie)) {
                // If not, add the movie node to the graph with an empty adjacency list
                actorAndMovie.put(movie, new ArrayList<>());
                actorAndMovie.get(movie).add(actor);

            } else if (actorAndMovie.containsKey(movie) && !actorAndMovie.get(movie).contains(actor)) {
                actorAndMovie.get(movie).add(actor);

            }
        }

        String start = "Kevin_Bacon_(I)";
        String end = "Christopher_Lee_(I)";
        actorAndMovie.forEach((k, v) -> {
            notVisited.add(k);
        });

        bfs(graph, actorAndMovie, start);

        actorAndMovie.forEach((k, v) -> {
            if (notVisited.contains(k)) {
                bfs(graph, actorAndMovie, k);
            }
        });

        printPath(parentMap, start, end);
        colorPath(graph, parentMap, start);

        // graph.getVertex(actor1).setColor("crimson");

        bridges.setDataStructure(graph);

        // Second visualize
        bridges.visualize();
    }
}
