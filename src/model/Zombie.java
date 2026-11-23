package model;

public class Zombie {

    public enum State {
        IDLE,
        CHASE,
        ATTACK,
        DEAD
    }

    private final int id;
    private final ZombieType type;
    private int currentHp;
    private State state;
    private double distanceToPlayer;
    private double attackCooldown;

    public Zombie(int id, ZombieType type, double distanceToPlayer) {
        this(id, type, type.getMaxHp(), State.IDLE, distanceToPlayer, 0.0);
    }

    public Zombie(int id, ZombieType type, int currentHp, State state,
                  double distanceToPlayer, double attackCooldown) {
        this.id = id;
        this.type = type;
        this.currentHp = currentHp;
        this.state = state;
        this.distanceToPlayer = distanceToPlayer;
        this.attackCooldown = attackCooldown;
    }

    public int getId() {
        return id;
    }

    public ZombieType getType() {
        return type;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, type.getMaxHp()));
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getDistanceToPlayer() {
        return distanceToPlayer;
    }

    public void setDistanceToPlayer(double distanceToPlayer) {
        this.distanceToPlayer = Math.max(0.0, distanceToPlayer);
    }

    public double getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(double attackCooldown) {
        this.attackCooldown = Math.max(0.0, attackCooldown);
    }

    public boolean isDead() {
        return state == State.DEAD || currentHp <= 0;
    }

    @Override
    public String toString() {
        return "Zombie{id=" + id
                + ", type=" + type
                + ", hp=" + currentHp + "/" + type.getMaxHp()
                + ", state=" + state
                + ", distance=" + distanceToPlayer
                + ", cooldown=" + attackCooldown + "}";
    }
}