package combat;

import model.Zombie;
import model.ZombieType;

import java.util.Objects;
import java.util.Random;

/**
 * Bộ máy chiến đấu thuần logic (không phụ thuộc UI).
 *
 * <p>Engine không giữ trạng thái: toàn bộ dữ liệu của trận nằm trong
 * {@link CombatEncounter}, nên một engine có thể phục vụ nhiều trận.
 *
 * <p>Quy ước đơn vị: khoảng cách tính theo "mét" game, tốc độ là mét/giây,
 * hồi chiêu của zombie tính bằng giây, còn deltaMs và các bộ đếm nội bộ tính bằng ms.
 */
public class CombatEngine {

    // ---------- Khởi tạo trận ----------
    private static final int DEFAULT_MAX_PLAYER_HP = 100;
    private static final double SPAWN_DISTANCE_MIN = 10.0;
    private static final double SPAWN_DISTANCE_MAX = 18.0;
    private static final int INITIAL_ACTIVE = 3;

    // ---------- Xuất hiện zombie ----------
    private static final int BASE_MAX_ACTIVE = 4;
    private static final int MAX_ACTIVE_CAP = 10;
    private static final long BASE_SPAWN_INTERVAL_MS = 3000L;
    private static final long MIN_SPAWN_INTERVAL_MS = 500L;
    private static final long SPAWN_REDUCTION_PER_STAGE_MS = 150L;
    private static final long SPAWN_REDUCTION_PER_NOISE_MS = 15L;

    // ---------- Cập nhật theo thời gian ----------
    private static final long MAX_STEP_MS = 250L;
    private static final double IDLE_SPEED_FACTOR = 0.25;
    private static final double IDLE_WAKE_DISTANCE = 6.0;
    private static final int IDLE_WAKE_NOISE = 20;
    private static final double ZOMBIE_ATTACK_COOLDOWN_SEC = 1.5;
    private static final double DAMAGE_BONUS_PER_STAGE = 0.10;
    private static final long NOISE_DECAY_INTERVAL_MS = 1000L;
    private static final int NOISE_DECAY_PER_INTERVAL = 5;
    private static final int MAX_NOISE = 100;

    // ---------- Hành động người chơi ----------
    private static final int SHOOT_DAMAGE = 35;
    private static final int SHOOT_NOISE = 15;
    private static final double KNIFE_RANGE = 2.5;
    private static final int KNIFE_DAMAGE = 50;
    private static final int KNIFE_NOISE = 3;
    private static final double BOMB_RADIUS = 8.0;
    private static final int BOMB_MAX_DAMAGE = 150;
    private static final int BOMB_MIN_DAMAGE = 60;
    private static final int BOMB_NOISE = 40;
    private static final int MEDKIT_HEAL = 40;
    private static final long SERUM_DURATION_MS = 10_000L;
    private static final double SERUM_DAMAGE_MULTIPLIER = 0.5;

    // ---------- Hồi chiêu vũ khí (ms) ----------
    private static final long SHOOT_COOLDOWN_MS = 400L;
    private static final long KNIFE_COOLDOWN_MS = 600L;
    private static final long BOMB_COOLDOWN_MS = 1500L;

    // =====================================================================
    //  API công khai
    // =====================================================================

    /**
     * Khởi tạo trận đấu: sinh danh sách zombie theo số lượng, cấp độ và seed.
     * Một vài zombie đầu tiên xuất hiện ngay, số còn lại chờ trong hàng đợi.
     *
     * @throws IllegalArgumentException nếu zombieCount hoặc playerHp không dương
     */
    public CombatEncounter startEncounter(EncounterRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.getZombieCount() <= 0) {
            throw new IllegalArgumentException("zombieCount phải > 0");
        }
        if (request.getPlayerHp() <= 0) {
            throw new IllegalArgumentException("playerHp phải > 0");
        }

        int stage = Math.max(1, request.getStageLevel());
        Random rng = new Random(request.getSeed());

        CombatEncounter encounter = new CombatEncounter();
        encounter.setStageLevel(stage);
        encounter.setMaxPlayerHp(Math.max(DEFAULT_MAX_PLAYER_HP, request.getPlayerHp()));
        encounter.setPlayerHp(request.getPlayerHp());
        encounter.setAmmoCount(request.getAmmoCount());
        encounter.setBombCount(request.getBombCount());
        encounter.setMedkitCount(request.getMedkitCount());
        encounter.setSerumCount(request.getSerumCount());

        // Phân bổ loại zombie theo cấp độ: càng cao càng nhiều RUNNER và TANK.
        int tankWeight = Math.min(30, 3 + 4 * (stage - 1));
        int runnerWeight = Math.min(40, 20 + 3 * (stage - 1));
        int walkerWeight = 100 - tankWeight - runnerWeight;

        for (int i = 1; i <= request.getZombieCount(); i++) {
            int roll = rng.nextInt(100);
            ZombieType type;
            if (roll < walkerWeight) {
                type = ZombieType.WALKER;
            } else if (roll < walkerWeight + runnerWeight) {
                type = ZombieType.RUNNER;
            } else {
                type = ZombieType.TANK;
            }
            double distance = SPAWN_DISTANCE_MIN
                    + rng.nextDouble() * (SPAWN_DISTANCE_MAX - SPAWN_DISTANCE_MIN);
            encounter.getSpawnQueue().add(new Zombie(i, type, distance));
        }

        int initial = Math.min(INITIAL_ACTIVE, request.getZombieCount());
        for (int i = 0; i < initial; i++) {
            encounter.getActiveZombies().add(encounter.getSpawnQueue().poll());
        }
        encounter.setSpawnTimerMs(spawnIntervalMs(encounter));

        return encounter;
    }

    /**
     * Cập nhật trận theo thời gian thực. Nếu đang pause hoặc đã kết thúc
     * (hay deltaMs không dương) thì trả về snapshot hiện tại, không thay đổi gì.
     */
    public CombatSnapshot tick(CombatEncounter encounter, long deltaMs) {
        Objects.requireNonNull(encounter, "encounter");
        if (encounter.isFinished() || encounter.isPaused() || deltaMs <= 0) {
            return snapshot(encounter);
        }

        // Chia nhỏ bước để deltaMs lớn không làm zombie "xuyên" qua người chơi.
        long remaining = deltaMs;
        while (remaining > 0 && !encounter.isFinished()) {
            long step = Math.min(remaining, MAX_STEP_MS);
            remaining -= step;
            advance(encounter, step);
            evaluateEnd(encounter);
        }
        return snapshot(encounter);
    }

    /**
     * Xử lý thao tác của người chơi. PAUSE luôn đảo trạng thái tạm dừng;
     * các hành động khác bị bỏ qua khi đang pause hoặc khi trận đã kết thúc.
     * Hành động không đủ điều kiện (hết đạn, không có mục tiêu, vũ khí còn hồi chiêu...)
     * bị bỏ qua và không tốn vật phẩm; hồi chiêu chỉ bắt đầu khi hành động thực sự được thực hiện.
     * Hồi chiêu giảm dần trong {@link #tick}, nên đứng yên khi đang pause.
     */
    public CombatSnapshot applyAction(CombatEncounter encounter, CombatAction action) {
        Objects.requireNonNull(encounter, "encounter");
        Objects.requireNonNull(action, "action");

        if (encounter.isFinished()) {
            return snapshot(encounter);
        }
        if (action == CombatAction.PAUSE) {
            encounter.setPaused(!encounter.isPaused());
            return snapshot(encounter);
        }
        if (encounter.isPaused()) {
            return snapshot(encounter);
        }

        switch (action) {
            case SHOOT:
                doShoot(encounter);
                break;
            case KNIFE:
                doKnife(encounter);
                break;
            case BOMB:
                doBomb(encounter);
                break;
            case HEAL:
                doHeal(encounter);
                break;
            case USE_SERUM:
                doUseSerum(encounter);
                break;
            default:
                break;
        }

        evaluateEnd(encounter);
        return snapshot(encounter);
    }

    /**
     * Đóng gói kết quả cuối cùng của trận.
     *
     * @throws IllegalStateException nếu trận chưa kết thúc (còn zombie và người chơi còn máu)
     */
    public CombatResult finish(CombatEncounter encounter) {
        Objects.requireNonNull(encounter, "encounter");
        evaluateEnd(encounter);
        if (!encounter.isFinished()) {
            throw new IllegalStateException("Trận đấu chưa kết thúc: " + encounter);
        }
        return new CombatResult(
                encounter.isVictory(),
                encounter.getPlayerHp(),
                encounter.getAmmoUsed(),
                encounter.getBombsUsed(),
                encounter.getMedkitsUsed(),
                encounter.getSerumsUsed());
    }

    // =====================================================================
    //  Cập nhật theo thời gian
    // =====================================================================

    private void advance(CombatEncounter e, long stepMs) {
        double dt = stepMs / 1000.0;

        if (e.isSerumActive()) {
            e.setSerumRemainingMs(e.getSerumRemainingMs() - stepMs);
        }

        e.setShootCooldownMs(e.getShootCooldownMs() - stepMs);
        e.setKnifeCooldownMs(e.getKnifeCooldownMs() - stepMs);
        e.setBombCooldownMs(e.getBombCooldownMs() - stepMs);

        e.setNoiseDecayMs(e.getNoiseDecayMs() + stepMs);
        while (e.getNoiseDecayMs() >= NOISE_DECAY_INTERVAL_MS) {
            e.setNoiseDecayMs(e.getNoiseDecayMs() - NOISE_DECAY_INTERVAL_MS);
            e.setNoiseLevel(e.getNoiseLevel() - NOISE_DECAY_PER_INTERVAL);
        }

        trySpawn(e, stepMs);

        for (Zombie zombie : e.getActiveZombies()) {
            if (e.getPlayerHp() <= 0) {
                break;
            }
            updateZombie(e, zombie, dt);
        }
    }

    private void trySpawn(CombatEncounter e, long stepMs) {
        if (e.getSpawnQueue().isEmpty()) {
            return;
        }
        e.setSpawnTimerMs(e.getSpawnTimerMs() - stepMs);

        boolean noOneOnField = e.getActiveZombies().isEmpty();
        boolean hasRoom = e.getActiveZombies().size() < maxActive(e.getStageLevel());
        if (noOneOnField || (e.getSpawnTimerMs() <= 0 && hasRoom)) {
            e.getActiveZombies().add(e.getSpawnQueue().poll());
            e.setSpawnTimerMs(spawnIntervalMs(e));
        }
    }

    private void updateZombie(CombatEncounter e, Zombie zombie, double dt) {
        if (zombie.isDead()) {
            return;
        }
        ZombieType type = zombie.getType();
        double range = type.getAttackRange();
        double distance = zombie.getDistanceToPlayer();

        zombie.setAttackCooldown(zombie.getAttackCooldown() - dt);

        // IDLE: lững thững tiến lại, bị đánh thức bởi tiếng ồn hoặc khi đến gần.
        if (zombie.getState() == Zombie.State.IDLE) {
            if (e.getNoiseLevel() >= IDLE_WAKE_NOISE || distance <= IDLE_WAKE_DISTANCE) {
                zombie.setState(Zombie.State.CHASE);
            } else {
                zombie.setDistanceToPlayer(
                        Math.max(range, distance - type.getSpeed() * IDLE_SPEED_FACTOR * dt));
                return;
            }
        }

        if (distance > range) {
            distance = Math.max(range, distance - type.getSpeed() * dt);
            zombie.setDistanceToPlayer(distance);
        }

        if (distance <= range) {
            zombie.setState(Zombie.State.ATTACK);
            if (zombie.getAttackCooldown() <= 0) {
                hitPlayer(e, zombie);
                zombie.setAttackCooldown(ZOMBIE_ATTACK_COOLDOWN_SEC);
            }
        } else {
            zombie.setState(Zombie.State.CHASE);
        }
    }

    private void hitPlayer(CombatEncounter e, Zombie zombie) {
        double damage = zombie.getType().getDamage()
                * (1.0 + DAMAGE_BONUS_PER_STAGE * (e.getStageLevel() - 1));
        if (e.isSerumActive()) {
            damage *= SERUM_DAMAGE_MULTIPLIER;
        }
        int finalDamage = Math.max(1, (int) Math.round(damage));
        e.setPlayerHp(e.getPlayerHp() - finalDamage);
    }

    // =====================================================================
    //  Hành động của người chơi
    // =====================================================================

    private void doShoot(CombatEncounter e) {
        if (e.getShootCooldownMs() > 0 || e.getAmmoCount() <= 0) {
            return;
        }
        Zombie target = nearestAlive(e, Double.MAX_VALUE);
        if (target == null) {
            return;
        }
        e.setAmmoCount(e.getAmmoCount() - 1);
        e.setAmmoUsed(e.getAmmoUsed() + 1);
        addNoise(e, SHOOT_NOISE);
        e.setShootCooldownMs(SHOOT_COOLDOWN_MS);
        damageZombie(target, SHOOT_DAMAGE);
    }

    private void doKnife(CombatEncounter e) {
        if (e.getKnifeCooldownMs() > 0) {
            return;
        }
        Zombie target = nearestAlive(e, KNIFE_RANGE);
        if (target == null) {
            return;
        }
        addNoise(e, KNIFE_NOISE);
        e.setKnifeCooldownMs(KNIFE_COOLDOWN_MS);
        damageZombie(target, KNIFE_DAMAGE);
    }

    /**
     * Ném bom vào zombie gần nhất. Zombie nào cách tâm nổ không quá BOMB_RADIUS
     * đều chịu sát thương, giảm dần từ BOMB_MAX_DAMAGE ở tâm xuống BOMB_MIN_DAMAGE ở rìa.
     */
    private void doBomb(CombatEncounter e) {
        if (e.getBombCooldownMs() > 0 || e.getBombCount() <= 0) {
            return;
        }
        Zombie center = nearestAlive(e, Double.MAX_VALUE);
        if (center == null) {
            return;
        }
        e.setBombCount(e.getBombCount() - 1);
        e.setBombsUsed(e.getBombsUsed() + 1);
        addNoise(e, BOMB_NOISE);
        e.setBombCooldownMs(BOMB_COOLDOWN_MS);

        double blastPoint = center.getDistanceToPlayer();
        for (Zombie zombie : e.getActiveZombies()) {
            if (zombie.isDead()) {
                continue;
            }
            double offset = Math.abs(zombie.getDistanceToPlayer() - blastPoint);
            if (offset <= BOMB_RADIUS) {
                double ratio = offset / BOMB_RADIUS;
                int damage = (int) Math.round(
                        BOMB_MAX_DAMAGE - (BOMB_MAX_DAMAGE - BOMB_MIN_DAMAGE) * ratio);
                damageZombie(zombie, damage);
            }
        }
    }

    private void doHeal(CombatEncounter e) {
        if (e.getMedkitCount() <= 0 || e.getPlayerHp() >= e.getMaxPlayerHp()) {
            return;
        }
        e.setMedkitCount(e.getMedkitCount() - 1);
        e.setMedkitsUsed(e.getMedkitsUsed() + 1);
        e.setPlayerHp(e.getPlayerHp() + MEDKIT_HEAL);
    }

    /**
     * Serum: trong SERUM_DURATION_MS, sát thương zombie gây ra giảm một nửa.
     * Không dùng chồng khi hiệu lực còn đang chạy (không tốn serum).
     */
    private void doUseSerum(CombatEncounter e) {
        if (e.getSerumCount() <= 0 || e.isSerumActive()) {
            return;
        }
        e.setSerumCount(e.getSerumCount() - 1);
        e.setSerumsUsed(e.getSerumsUsed() + 1);
        e.setSerumRemainingMs(SERUM_DURATION_MS);
    }

    // =====================================================================
    //  Tiện ích nội bộ
    // =====================================================================

    private Zombie nearestAlive(CombatEncounter e, double maxDistance) {
        Zombie nearest = null;
        for (Zombie zombie : e.getActiveZombies()) {
            if (zombie.isDead() || zombie.getDistanceToPlayer() > maxDistance) {
                continue;
            }
            if (nearest == null
                    || zombie.getDistanceToPlayer() < nearest.getDistanceToPlayer()) {
                nearest = zombie;
            }
        }
        return nearest;
    }

    private void damageZombie(Zombie zombie, int damage) {
        zombie.setCurrentHp(zombie.getCurrentHp() - damage);
        if (zombie.getCurrentHp() <= 0) {
            zombie.setState(Zombie.State.DEAD);
        }
    }

    private void addNoise(CombatEncounter e, int amount) {
        e.setNoiseLevel(Math.min(MAX_NOISE, e.getNoiseLevel() + amount));
    }

    private int maxActive(int stageLevel) {
        return Math.min(MAX_ACTIVE_CAP, BASE_MAX_ACTIVE + stageLevel / 2);
    }

    private long spawnIntervalMs(CombatEncounter e) {
        long interval = BASE_SPAWN_INTERVAL_MS
                - SPAWN_REDUCTION_PER_STAGE_MS * e.getStageLevel()
                - SPAWN_REDUCTION_PER_NOISE_MS * e.getNoiseLevel();
        return Math.max(MIN_SPAWN_INTERVAL_MS, interval);
    }

    /** Dọn zombie đã chết và xác định thắng/thua. Người chơi chết được ưu tiên xét trước. */
    private void evaluateEnd(CombatEncounter e) {
        e.getActiveZombies().removeIf(zombie -> {
            if (zombie.isDead()) {
                zombie.setState(Zombie.State.DEAD);
                return true;
            }
            return false;
        });

        if (e.isFinished()) {
            return;
        }
        if (e.getPlayerHp() <= 0) {
            e.setFinished(true);
            e.setVictory(false);
        } else if (e.getRemainingTotalZombies() == 0) {
            e.setFinished(true);
            e.setVictory(true);
        }
    }

    private CombatSnapshot snapshot(CombatEncounter e) {
        return new CombatSnapshot(
                e.getPlayerHp(),
                e.getActiveZombies(),
                e.getRemainingTotalZombies(),
                e.getNoiseLevel(),
                e.getAmmoCount(),
                e.isPaused(),
                e.isFinished(),
                e.isVictory());
    }
}