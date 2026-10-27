package model;

public class Edge {
    private final Node destination;
    private final RoadStatus status;
    private final int distance;

    public Edge(Node destination, RoadStatus status, int distance) {
        this.destination = destination;
        this.status = status;
        this.distance = distance;
    }

    public Node getDestination() {
        return destination;
    }
    public RoadStatus getStatus() {
        return status;
    }
    public int getDistance() {
        return distance;
    }

    @Override 
    public String toString() {
        return "--" + status + "(" + distance + ")--> " + destination;
    }
}
