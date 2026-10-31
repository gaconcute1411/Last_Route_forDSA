package model;

public class Node {
    private final String name;
    private int survivorCount;
    private int x;  // thêm tọa độ x và y để vẽ giao diện bản đồ
    private int y;

    public Node(String name) {
        this(name, 0, 0);
    }

    public Node(String name, int x, int y) {
        this.name = name;
        this.survivorCount = 0;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }
    public int getSurvivorCount() {
        return survivorCount;
    }
    public void setSurvivorCount(int count) {
        this.survivorCount = count;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override 
    public String toString() {
        return name;
    }
}
