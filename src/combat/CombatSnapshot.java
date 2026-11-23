package combat;

import model.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombatSnapshot {

    private int playerHp;
    private List<Zombie> activeZombies;
    private int remainingTotalZombies;
    private int noiseLevel;
    private int ammoCount;
    private boolean paused;
    private boolean finished;
    private boolean victory;

    public CombatSnapshot() {
        this.activeZombies = new ArrayList<>();
    }

    public CombatSnapshot(int playerHp, List<Zombie> activeZombies, int remainingTotalZombies,
                          int noiseLevel, int ammoCount,
                          boolean paused, boolean finished, boolean victory) {
        this.playerHp = playerHp;
        this.activeZombies = activeZombies == null
                ? new ArrayList<>()
                : new ArrayList<>(activeZombies);
        this.remainingTotalZombies = remainingTotalZombies;
        this.noiseLevel = noiseLevel;
        this.ammoCount = ammoCount;
        this.paused = paused;
        this.finished = finished;
        this.victory = victory;
    }

    public int getPlayerHp() {
        return playerHp;
    }

    public void setPlayerHp(int playerHp) {
        this.playerHp = playerHp;
    }

    public List<Zombie> getActiveZombies() {
        return Collections.unmodifiableList(activeZombies);
    }

    public void setActiveZombies(List<Zombie> activeZombies) {
        this.activeZombies = activeZombies == null
                ? new ArrayList<>()
                : new ArrayList<>(activeZombies);
    }

    public int getRemainingTotalZombies() {
        return remainingTotalZombies;
    }

    public void setRemainingTotalZombies(int remainingTotalZombies) {
        this.remainingTotalZombies = remainingTotalZombies;
    }

    public int getNoiseLevel() {
        return noiseLevel;
    }

    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

   @Override
public String toString() {
    return "CombatSnapshot{playerHp=" + playerHp
            + ", activeZombies=" + activeZombies.size()
            + ", remainingTotalZombies=" + remainingTotalZombies
            + ", noiseLevel=" + noiseLevel
            + ", ammoCount=" + ammoCount
            + ", paused=" + paused
            + ", finished=" + finished
            + ", victory=" + victory + "}";
}
}