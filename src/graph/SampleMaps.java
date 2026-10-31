package graph;

import model.RoadStatus;

import java.util.List;

public class SampleMaps {
    // 3 safe zone của bản đồ
    public static final List<String> SAFE_ZONES = List.of("SafeZone", "Stadium", "Bunker");

    // tạo bản đồ mẫu gồm: 13 địa điểm, 22 đường, 15 survivor
    public static Graph createCity() {
        Graph city = new Graph();

        // thêm các địa điểm với tên, x, y
        city.addNode("Hospital", 100, 80);
        city.addNode("Factory", 260, 60);
        city.addNode("Highway", 420, 110);
        city.addNode("School", 580, 70);
        city.addNode("Stadium", 720, 160); // safe zone
        city.addNode("Store", 110, 230);
        city.addNode("Apartment", 260,220);
        city.addNode("Police", 420, 260);
        city.addNode("Airport", 600, 300);
        city.addNode("Church", 100, 390);
        city.addNode("Harbor", 290, 400);
        city.addNode("Bunker", 730, 420);       // Safe Zone
        city.addNode("SafeZone", 40, 310);      // Safe Zone

        // survivor (tổng 15 survivor)
        city.getNode("School").setSurvivorCount(3);
        city.getNode("Hospital").setSurvivorCount(2);
        city.getNode("Apartment").setSurvivorCount(1);
        city.getNode("Airport").setSurvivorCount(2);
        city.getNode("Church").setSurvivorCount(2);
        city.getNode("Harbor").setSurvivorCount(2);
        city.getNode("Factory").setSurvivorCount(1);
        city.getNode("Police").setSurvivorCount(2);

        // Đường: from, to, trạng thái, khoảng cách
        city.addRoad("Hospital", "Factory", RoadStatus.SAFE, 4);
        city.addRoad("Hospital", "Store", RoadStatus.DANGEROUS, 3);
        city.addRoad("Hospital", "Apartment", RoadStatus.SAFE, 5);
        city.addRoad("Factory", "Highway", RoadStatus.SAFE, 5);
        city.addRoad("Factory", "Apartment", RoadStatus.DANGEROUS, 4);
        city.addRoad("Highway", "School", RoadStatus.INFESTED, 6);
        city.addRoad("Highway", "Police", RoadStatus.SAFE, 4);
        city.addRoad("Highway", "Stadium", RoadStatus.DANGEROUS, 7);
        city.addRoad("School", "Stadium", RoadStatus.SAFE, 5);
        city.addRoad("Store", "SafeZone", RoadStatus.SAFE, 2);
        city.addRoad("Store", "Apartment", RoadStatus.SAFE, 4);
        city.addRoad("Store", "Church", RoadStatus.DANGEROUS, 5);
        city.addRoad("Apartment", "Police", RoadStatus.SAFE, 4);
        city.addRoad("Apartment", "Harbor", RoadStatus.INFESTED, 5);
        city.addRoad("Police", "Airport", RoadStatus.DANGEROUS, 5);
        city.addRoad("Police", "Harbor", RoadStatus.SAFE, 5);
        city.addRoad("Airport", "Stadium", RoadStatus.INFESTED, 6);
        city.addRoad("Airport", "Bunker", RoadStatus.SAFE, 4);
        city.addRoad("Harbor", "Church", RoadStatus.SAFE, 4);
        city.addRoad("Harbor", "Bunker", RoadStatus.BLOCKED, 8);
        city.addRoad("Church", "SafeZone", RoadStatus.SAFE, 3);
        city.addRoad("School", "Airport", RoadStatus.BLOCKED, 7);

        return city;
    }
}
