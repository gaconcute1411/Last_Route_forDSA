import graph.Graph;
import model.RoadStatus;
import graph.BFS;
import graph.RouteStrategy;
public class Main {
    public static void main(String[] args) {
        Graph city = new Graph();

        city.addNode("Hospital");
        city.addNode("Factory");
        city.addNode("Store");
        city.addNode("Highway");
        city.addNode("School");
        city.addNode("SafeZone");

        city.addRoad("Hospital", "Factory", RoadStatus.SAFE, 4);
        city.addRoad("Hospital", "Store", RoadStatus.DANGEROUS, 3);
        city.addRoad("Factory", "Highway", RoadStatus.SAFE, 5);
        city.addRoad("Highway", "School", RoadStatus.INFESTED, 6);
        city.addRoad("Store", "SafeZone", RoadStatus.SAFE, 2);
        city.addRoad("School", "SafeZone", RoadStatus.BLOCKED, 3);

        city.printMap();
        for (RouteStrategy strategy : RouteStrategy.values()) {
            System.out.println("== " + strategy);
            System.out.println("Hospital -> SafeZone: " + BFS.findRoute(city, "Hospital", "SafeZone", strategy));
            System.out.println("Hospital -> School: " + BFS.findRoute(city, "Hospital", "School", strategy));
            System.out.println("SafeZone -> Hospital: " + BFS.findRoute(city, "SafeZone", "Hospital", strategy));
        }
    }
}
