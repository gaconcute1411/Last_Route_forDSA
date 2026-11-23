package model;

public enum ZombieType {
    WALKER(100, 10, 1.0, 1.5),
    RUNNER(60, 8, 2.5, 1.2),
    TANK(300, 25, 0.7, 2.0);

    private final int maxHp;
    private final int damage;
    private final double speed;
    private final double attackRange;

    ZombieType(int maxHp, int damage, double speed, double attackRange) {
        this.maxHp = maxHp;
        this.damage = damage;
        this.speed = speed;
        this.attackRange = attackRange;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAttackRange() {
        return attackRange;
    }
}