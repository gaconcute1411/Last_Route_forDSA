package graph;

import model.Edge;
import model.Node;
import model.RoadStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    // name -> Node
    private final Map<String, Node> nodes = new HashMap<>();
    // name -> list of the roads leaving that node
    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();

    public Node addNode(String name) {
        Node node = new Node(name);
        nodes.put(name, node);
        adjacencyList.put(name, new ArrayList<>());
        return node;
    }

    public Node addNode(String name, int x, int y) {
        Node node = addNode(name);
        node.setPosition(x, y);
        return node;
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    public Set<String> getNodeNames() {
        return nodes.keySet();
    }

    // Đường hai chiều -> thêm một cạnh theo cả hai hướng
    public void addRoad(String from, String to, RoadStatus status, int distance) {
        Node a = nodes.get(from);
        Node b = nodes.get(to);
        if (a == null || b == null) {
            throw new IllegalArgumentException("Unknown node: " + from + " or " + to);
        }
        adjacencyList.get(from).add(new Edge(b, status, distance));
        // Cạnh ngược thuộc về node đích (to), vì đường đi được theo cả hai hướng
        adjacencyList.get(to).add(new Edge(a, status, distance));
    }

    public List<Edge> getNeighbors(String name) {
        return adjacencyList.get(name);
    }

    public void printMap() {
        for (String name : adjacencyList.keySet()) {
            System.out.println(name);
            for (Edge e : adjacencyList.get(name)) {
                System.out.println("  " + e);
            }
        }
    }
}

