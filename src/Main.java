import graph.BFS;
import graph.Graph;
import graph.RouteStrategy;
import graph.SampleMaps;

public class Main {
    public static void main(String[] args) {
        Graph city = SampleMaps.createCity();

        // Kiểm tra bản đồ có đủ quy mô không
        int roads = 0;
        int survivors = 0;
        for (String name : city.getNodeNames()) {
            roads += city.getNeighbors(name).size();
            survivors += city.getNode(name).getSurvivorCount();
        }
        System.out.println("Locations: " + city.getNodeNames().size());
        System.out.println("Roads: " + roads / 2);     // mỗi đường được lưu 2 lần
        System.out.println("Survivors: " + survivors);
        System.out.println();

        String[][] queries = {
            {"Hospital", "SafeZone"},
            {"Hospital", "School"},
            {"Hospital", "Bunker"},
            {"Harbor", "Bunker"}
        };

        for (String[] q : queries) {
            System.out.println(q[0] + " -> " + q[1]);
            for (RouteStrategy strategy : RouteStrategy.values()) {
                System.out.println("   " + strategy + ": "
                        + BFS.findRoute(city, q[0], q[1], strategy));
            }
        }
    }
}