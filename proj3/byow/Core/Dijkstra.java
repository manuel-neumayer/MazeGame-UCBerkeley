package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdDraw;

import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class Dijkstra {
    Graph graph;
    HashMap<Integer, int[]> vertexToPath;

    public Dijkstra(Graph graph) {
        this.graph = graph;
        vertexToPath = new HashMap<>();
    }

    public int[] shortestPath(int target) {
        if (vertexToPath.containsKey(target)) {
            return vertexToPath.get(target);
        }
        return findShortestPath(target);
    }

    private int[] findShortestPath(int target) {
        // in the fringe, we store int pairs: [vertex index, distance of vertex to source]
        // PriorityQueue<int[]> fringe = new PriorityQueue<>(Comparator.comparingInt(o -> o[1]));
        int[] distances = new int[graph.V()];
        int[] edgeTo = new int[graph.V()];
        for (int vertex = 0; vertex < graph.V(); vertex++) {
            int distance;
            if (vertex != target) {
                distance = MAX_VALUE;
            } else {
                distance = 0;
            }
            distances[vertex] = distance;
            //int[] vertexDistancePair = new int[]{vertex, distance};
            //fringe.add(vertexDistancePair);
        }
        int currentVertex = target;
        edgeTo[currentVertex] = currentVertex;
        while (true) {

            //Position position = new Position(currentVertex % Crawler.w, currentVertex / Crawler.w);
            //grid[position.x()][position.y()] = Tileset.FLOOR;
            //ter.renderFrame(grid);
            //StdDraw.pause(100);

            int newDistance = distances[currentVertex] + 1;
            Iterator<Integer> adjacentIterator = graph.adj(currentVertex).iterator();
            while (adjacentIterator.hasNext()) {
                int adjacentVertex = adjacentIterator.next();
                //System.out.println(adjacentVertex + " is adjacent to " + currentVertex);
                if (newDistance < distances[adjacentVertex]) {
                    distances[adjacentVertex] = newDistance;
                    edgeTo[adjacentVertex] = currentVertex;
                }
            }
            distances[currentVertex] = -1;
            currentVertex = findSmallest(distances);
            //System.out.println("new smallest: " + currentVertex);
            if (currentVertex == -1) {
                break;
            }
        }
        vertexToPath.put(target, edgeTo);
        return edgeTo;
    }

    /*
    Return the index of the smallest positive integer in the array.
    */
    private int findSmallest(int[] distances) {
        int smallestDistance = MAX_VALUE;
        int smallest = -1;
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] >= 0 && distances[i] < smallestDistance) {
                smallestDistance = distances[i];
                smallest = i;
            }
        }
        return smallest;
    }

}
