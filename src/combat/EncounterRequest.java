package combat;

public class EncounterRequest {

    private int zombieCount;
    private int stageLevel;
    private long seed;
    private int playerHp;
    private int ammoCount;
    private int bombCount;
    private int medkitCount;
    private int serumCount;

    public EncounterRequest() {
    }

    public EncounterRequest(int zombieCount, int stageLevel, long seed, int playerHp,
                            int ammoCount, int bombCount, int medkitCount, int serumCount) {
        this.zombieCount = zombieCount;
        this.stageLevel = stageLevel;
        this.seed = seed;
        this.playerHp = playerHp;
        this.ammoCount = ammoCount;
        this.bombCount = bombCount;
        this.medkitCount = medkitCount;
        this.serumCount = serumCount;
    }

    public int getZombieCount() {
        return zombieCount;
    }

    public void setZombieCount(int zombieCount) {
        this.zombieCount = zombieCount;
    }

    public int getStageLevel() {
        return stageLevel;
    }

    public void setStageLevel(int stageLevel) {
        this.stageLevel = stageLevel;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getPlayerHp() {
        return playerHp;
    }

    public void setPlayerHp(int playerHp) {
        this.playerHp = playerHp;
    }

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    public int getBombCount() {
        return bombCount;
    }

    public void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    public int getMedkitCount() {
        return medkitCount;
    }

    public void setMedkitCount(int medkitCount) {
        this.medkitCount = medkitCount;
    }

    public int getSerumCount() {
        return serumCount;
    }

    public void setSerumCount(int serumCount) {
        this.serumCount = serumCount;
    }

    @Override
    public String toString() {
        return "EncounterRequest{zombieCount=" + zombieCount
                + ", stageLevel=" + stageLevel
                + ", seed=" + seed
                + ", playerHp=" + playerHp
                + ", ammoCount=" + ammoCount
                + ", bombCount=" + bombCount
                + ", medkitCount=" + medkitCount
                + ", serumCount=" + serumCount + "}";
    }
}