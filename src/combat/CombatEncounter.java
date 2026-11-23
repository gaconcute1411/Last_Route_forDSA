package combat;

import model.Zombie;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

/**
 * Trạng thái nội bộ của một lượt chạm trán: zombie đang hoạt động,
 * hàng đợi zombie chưa xuất hiện, mức độ ồn, cờ tạm dừng, cùng trạng thái
 * của người chơi (máu, vật phẩm) và các bộ đếm thời gian do CombatEngine quản lý.
 */
public class CombatEncounter {

    private static final int DEFAULT_MAX_PLAYER_HP = 100;

    // --- Zombie ---
    private final List<Zombie> activeZombies;
    private final Queue<Zombie> spawnQueue;

    // --- Môi trường ---
    private int noiseLevel;
    private boolean paused;
    private int stageLevel;

    // --- Người chơi ---
    private int playerHp;
    private int maxPlayerHp;
    private int ammoCount;
    private int bombCount;
    private int medkitCount;
    private int serumCount;

    // --- Thống kê đã dùng ---
    private int ammoUsed;
    private int bombsUsed;
    private int medkitsUsed;
    private int serumsUsed;

    // --- Kết thúc ---
    private boolean finished;
    private boolean victory;

    // --- Bộ đếm thời gian (ms) ---
    private long spawnTimerMs;
    private long noiseDecayMs;
    private long serumRemainingMs;

    // --- Hồi chiêu vũ khí của người chơi (ms còn lại, 0 = sẵn sàng) ---
    private long shootCooldownMs;
    private long knifeCooldownMs;
    private long bombCooldownMs;

    public CombatEncounter() {
        this.activeZombies = new ArrayList<>();
        this.spawnQueue = new ArrayDeque<>();
        this.noiseLevel = 0;
        this.paused = false;
        this.stageLevel = 1;
        this.maxPlayerHp = DEFAULT_MAX_PLAYER_HP;
        this.playerHp = DEFAULT_MAX_PLAYER_HP;
    }

    public CombatEncounter(Collection<Zombie> activeZombies, Collection<Zombie> spawnQueue,
                           int noiseLevel, boolean paused) {
        this();
        if (activeZombies != null) {
            this.activeZombies.addAll(activeZombies);
        }
        if (spawnQueue != null) {
            this.spawnQueue.addAll(spawnQueue);
        }
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

    public int getStageLevel() {
        return stageLevel;
    }

    public void setStageLevel(int stageLevel) {
        this.stageLevel = Math.max(1, stageLevel);
    }

    public int getPlayerHp() {
        return playerHp;
    }

    public void setPlayerHp(int playerHp) {
        this.playerHp = Math.max(0, Math.min(playerHp, maxPlayerHp));
    }

    public int getMaxPlayerHp() {
        return maxPlayerHp;
    }

    public void setMaxPlayerHp(int maxPlayerHp) {
        this.maxPlayerHp = Math.max(1, maxPlayerHp);
        this.playerHp = Math.min(this.playerHp, this.maxPlayerHp);
    }

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = Math.max(0, ammoCount);
    }

    public int getBombCount() {
        return bombCount;
    }

    public void setBombCount(int bombCount) {
        this.bombCount = Math.max(0, bombCount);
    }

    public int getMedkitCount() {
        return medkitCount;
    }

    public void setMedkitCount(int medkitCount) {
        this.medkitCount = Math.max(0, medkitCount);
    }

    public int getSerumCount() {
        return serumCount;
    }

    public void setSerumCount(int serumCount) {
        this.serumCount = Math.max(0, serumCount);
    }

    public int getAmmoUsed() {
        return ammoUsed;
    }

    public void setAmmoUsed(int ammoUsed) {
        this.ammoUsed = Math.max(0, ammoUsed);
    }

    public int getBombsUsed() {
        return bombsUsed;
    }

    public void setBombsUsed(int bombsUsed) {
        this.bombsUsed = Math.max(0, bombsUsed);
    }

    public int getMedkitsUsed() {
        return medkitsUsed;
    }

    public void setMedkitsUsed(int medkitsUsed) {
        this.medkitsUsed = Math.max(0, medkitsUsed);
    }

    public int getSerumsUsed() {
        return serumsUsed;
    }

    public void setSerumsUsed(int serumsUsed) {
        this.serumsUsed = Math.max(0, serumsUsed);
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

    public long getSpawnTimerMs() {
        return spawnTimerMs;
    }

    public void setSpawnTimerMs(long spawnTimerMs) {
        this.spawnTimerMs = Math.max(0L, spawnTimerMs);
    }

    public long getNoiseDecayMs() {
        return noiseDecayMs;
    }

    public void setNoiseDecayMs(long noiseDecayMs) {
        this.noiseDecayMs = Math.max(0L, noiseDecayMs);
    }

    public long getSerumRemainingMs() {
        return serumRemainingMs;
    }

    public void setSerumRemainingMs(long serumRemainingMs) {
        this.serumRemainingMs = Math.max(0L, serumRemainingMs);
    }

    public long getShootCooldownMs() {
        return shootCooldownMs;
    }

    public void setShootCooldownMs(long shootCooldownMs) {
        this.shootCooldownMs = Math.max(0L, shootCooldownMs);
    }

    public long getKnifeCooldownMs() {
        return knifeCooldownMs;
    }

    public void setKnifeCooldownMs(long knifeCooldownMs) {
        this.knifeCooldownMs = Math.max(0L, knifeCooldownMs);
    }

    public long getBombCooldownMs() {
        return bombCooldownMs;
    }

    public void setBombCooldownMs(long bombCooldownMs) {
        this.bombCooldownMs = Math.max(0L, bombCooldownMs);
    }

    /** Serum có đang phát huy tác dụng không. */
    public boolean isSerumActive() {
        return serumRemainingMs > 0;
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
                + ", paused=" + paused
                + ", playerHp=" + playerHp + "/" + maxPlayerHp
                + ", ammo=" + ammoCount
                + ", bombs=" + bombCount
                + ", medkits=" + medkitCount
                + ", serums=" + serumCount
                + ", finished=" + finished
                + ", victory=" + victory + "}";
    }
}