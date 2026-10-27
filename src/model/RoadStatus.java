package model;

public enum RoadStatus {
    SAFE, //
    DANGEROUS,
    INFESTED,
    BLOCKED;

    public boolean isPassable() {
        return this != BLOCKED;
    }
}
