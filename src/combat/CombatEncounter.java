package combat;

import model.Zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class CombatEncounter {

    private final List<Zombie> activeZombies;
    private final Queue<Zombie> spawnQueue;
    private int noiseLevel;
    private boolean paused;

    public CombatEncounter() {
        this.activeZombies = new ArrayList<>();
        this.spawnQueue = new ArrayDeque<>();
        this.noiseLevel = 0;
        this.paused = false;
    }

    public CombatEncounter(Collection<Zombie> activeZombies, Collection<Zombie> spawnQueue,
                           int noiseLevel, boolean paused) {
        this.activeZombies = activeZombies == null
                ? new ArrayList<>()
                : new ArrayList<>(activeZombies);
        this.spawnQueue = spawnQueue == null
                ? new ArrayDeque<>()
                : new ArrayDeque<>(spawnQueue);
        this.noiseLevel = noiseLevel;
        this.paused = paused;
    }

    /** Danh sách zombie đang hoạt động (có thể chỉnh sửa trực tiếp). */
    public List<Zombie> getActiveZombies() {
        return activeZombies;
    }

    /** Hàng đợi zombie chưa xuất hiện (có thể chỉnh sửa trực tiếp). */
    public Queue<Zombie> getSpawnQueue() {
        return spawnQueue;
    }

    public void setActiveZombies(Collection<Zombie> zombies) {
        this.activeZombies.clear();
        if (zombies != null) {
            this.activeZombies.addAll(zombies);
        }
    }

    public void setSpawnQueue(Collection<Zombie> zombies) {
        this.spawnQueue.clear();
        if (zombies != null) {
            this.spawnQueue.addAll(zombies);
        }
    }

    public int getNoiseLevel() {
        return noiseLevel;
    }

    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = Math.max(0, noiseLevel);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /** Tổng số zombie còn lại (đang hoạt động + chưa xuất hiện). */
    public int getRemainingTotalZombies() {
        return activeZombies.size() + spawnQueue.size();
    }

    @Override
    public String toString() {
        return "CombatEncounter{active=" + activeZombies.size()
                + ", queued=" + spawnQueue.size()
                + ", noiseLevel=" + noiseLevel
                + ", paused=" + paused + "}";
    }
}