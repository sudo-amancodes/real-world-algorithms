import java.util.List;

import bridges.base.Edge;
import bridges.base.GraphAdjListSimple;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.ActorMovieIMDB;

import java.util.ArrayList;
import java.util.HashMap;

// A test class that fetches a random movie from IMDB then sends the
//	actors name and movie title + release date to the console as output.
//	There is no visual output to the users BRIDGES gallery after running this code.

public class App {
    public static void main(String[] args) throws Exception {

        // create the Bridges object
        Bridges bridges = new Bridges(11, "amanw", System.getenv("API_KEY"));

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

                graph.addEdge(actor, movie);

            } else if (actorAndMovie.containsKey(actor) && !actorAndMovie.get(actor).contains(movie)) {
                actorAndMovie.get(actor).add(movie);
                graph.addEdge(actor, movie);
            }

            // Check if the movie node already exists in the graph
            if (!actorAndMovie.containsKey(movie)) {
                // If not, add the movie node to the graph with an empty adjacency list
                actorAndMovie.put(movie, new ArrayList<>());
                actorAndMovie.get(movie).add(actor);
                graph.addEdge(movie, actor);
            } else if (actorAndMovie.containsKey(movie) && !actorAndMovie.get(movie).contains(actor)) {
                actorAndMovie.get(movie).add(actor);

                graph.addEdge(movie, actor);
            }
        }
        bridges.setDataStructure(graph);

        // First visualize
        bridges.visualize();

        String actor1 = "Bill_Murray_(I)";
        for (Edge<String, String> edge : graph.outgoingEdgeSetOf(actor1)) {
            graph.getLinkVisualizer(edge.getFrom(), edge.getTo()).setColor("orange");

            graph.getVertex(edge.getTo()).setColor("crimson");

            graph.getLinkVisualizer(edge.getTo(), edge.getFrom()).setColor("orange");

        }

        String actor2 = "Christopher_Lee_(I)";
        for (Edge<String, String> edge : graph.outgoingEdgeSetOf(actor2)) {
            graph.getLinkVisualizer(edge.getFrom(), edge.getTo()).setColor("orange");

            graph.getVertex(edge.getTo()).setColor("cyan");

            graph.getLinkVisualizer(edge.getTo(), edge.getFrom()).setColor("orange");

        }
        graph.getVertex(actor1).setColor("crimson");

        graph.getVertex(actor2).setColor("cyan");

        bridges.setDataStructure(graph);

        // Second visualize
        bridges.visualize();
    }
}
