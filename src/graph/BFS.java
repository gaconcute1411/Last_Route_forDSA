package graph;

import model.Edge;
import model.Node;
import model.RoadStatus;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BFS {
    // đưa về route có ít road nhất, rỗng nếu không có đường đi
    public static LinkedList<Node> findRoute(Graph graph, String from, String to, RouteStrategy strategy) {
        LinkedList<Node> route = new LinkedList<>();
        if (graph.getNode(from) == null || graph.getNode(to) == null) {
            return route;
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>(); // node -> node đứng trước nó

        queue.add(from);
        visited.add(from);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(to)) {
                break;
            }
            for (Edge edge : graph.getNeighbors(current)) {
                String next = edge.getDestination().getName();
                if (!isAllowed(edge.getStatus(), strategy)) {
                    continue; // bỏ qua đường không hợp strategy
                }
                if (visited.contains(next)) {
                    continue; // đã đi qua rồi
                }
                visited.add(next);
                parent.put(next, current);
                queue.add(next);
            }
        }

        if (!visited.contains(to)) {
            return route; // không tới được
        }
        
        // lần ngược từ đích về điểm xuất phát để dựng route
        for (String at = to; at != null; at = parent.get(at)) {
            route.addFirst(graph.getNode(at));
        }
        return route;
    }
    
    private static boolean isAllowed(RoadStatus status, RouteStrategy strategy) {
         switch (strategy) {
            case SAFE:
                return status == RoadStatus.SAFE;
            case DIRECT:
                return status == RoadStatus.SAFE || status == RoadStatus.DANGEROUS;
            case EMERGENCY:
                return status.isPassable();
            default:
                return false;
         }
    }
}
