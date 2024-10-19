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

    public static void bfs(GraphAdjListSimple<String> graph, HashMap<String, ArrayList<String>> actorAndMovie,
            String current) {

        Queue<String> queue = new LinkedList<>();

        String[] colors = { "red", "green", "blue", "cyan", "magenta", "yellow", "mistyrose", "orange",
                "purple", "beige" };

        notVisited.remove(current);

        queue.add(current);

        int currentColor = 0;

        while (!queue.isEmpty()) {

            String popString = queue.remove();

            if (currentColor == 0) {
                graph.getVertex(popString).setColor(colors[currentColor]);
                graph.getVertex(popString).setLabel(popString + "(0)");
            }
            // else {
            // graph.getVertex(popString).setColor(colors[9]);
            // }

            for (String nextVertex : actorAndMovie.get(popString)) {
                if (notVisited.contains(nextVertex)) {

                    notVisited.remove(nextVertex);
                    queue.add(nextVertex);

                    graph.addEdge(popString, nextVertex);
                    graph.addEdge(nextVertex, popString);

                    for (int i = 0; i < colors.length; i++) {
                        Color loopColor = new Color();
                        loopColor.setColor(colors[i]);

                        if (loopColor.equals(graph.getVertex(popString).getColor())) {
                            graph.getVertex(nextVertex).setColor(colors[i + 1]);

                            graph.getLinkVisualizer(popString, nextVertex).setColor(colors[i]);

                            graph.getLinkVisualizer(nextVertex, popString).setColor(colors[i]);

                            int value = i + 1;

                            graph.getVertex(nextVertex).setLabel(nextVertex + "(" + value + ")");

                        }

                    }

                }
                // else {
                // graph.getVertex(nextVertex).setColor(colors[9]);

                // graph.getLinkVisualizer(popString, nextVertex).setColor(colors[9]);

                // graph.getLinkVisualizer(nextVertex, popString).setColor(colors[9]);
                // }

            }

            currentColor = 1;
        }

    }

    public static void main(String[] args) throws Exception {

        // create the Bridges object
        Bridges bridges = new Bridges(12, "amanw", System.getenv("API_KEY"));

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

        String start = "Cate_Blanchett";

        actorAndMovie.forEach((k, v) -> {
            notVisited.add(k);
        });

        bfs(graph, actorAndMovie, start);

        actorAndMovie.forEach((k, v) -> {
            if (notVisited.contains(k)) {
                bfs(graph, actorAndMovie, k);
            }
        });

        // graph.getVertex(actor1).setColor("crimson");

        bridges.setDataStructure(graph);

        // Second visualize
        bridges.visualize();
    }
}
