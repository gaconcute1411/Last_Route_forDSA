package model;

public class Node {
    private final String name;
    private int survivorCount;

    public Node(String name) {
        this.name = name;
        this.survivorCount = 0;
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

    @Override 
    public String toString() {
        return name;
    }
}
