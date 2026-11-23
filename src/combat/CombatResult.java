package combat;

public class CombatResult {

    private boolean victory;
    private int remainingPlayerHp;
    private int ammoUsed;
    private int bombsUsed;
    private int medkitsUsed;
    private int serumsUsed;

    public CombatResult() {
    }

    public CombatResult(boolean victory, int remainingPlayerHp, int ammoUsed,
                        int bombsUsed, int medkitsUsed, int serumsUsed) {
        this.victory = victory;
        this.remainingPlayerHp = remainingPlayerHp;
        this.ammoUsed = ammoUsed;
        this.bombsUsed = bombsUsed;
        this.medkitsUsed = medkitsUsed;
        this.serumsUsed = serumsUsed;
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public int getRemainingPlayerHp() {
        return remainingPlayerHp;
    }

    public void setRemainingPlayerHp(int remainingPlayerHp) {
        this.remainingPlayerHp = remainingPlayerHp;
    }

    public int getAmmoUsed() {
        return ammoUsed;
    }

    public void setAmmoUsed(int ammoUsed) {
        this.ammoUsed = ammoUsed;
    }

    public int getBombsUsed() {
        return bombsUsed;
    }

    public void setBombsUsed(int bombsUsed) {
        this.bombsUsed = bombsUsed;
    }

    public int getMedkitsUsed() {
        return medkitsUsed;
    }

    public void setMedkitsUsed(int medkitsUsed) {
        this.medkitsUsed = medkitsUsed;
    }

    public int getSerumsUsed() {
        return serumsUsed;
    }

    public void setSerumsUsed(int serumsUsed) {
        this.serumsUsed = serumsUsed;
    }

    @Override
    public String toString() {
        return "CombatResult{victory=" + victory
                + ", remainingPlayerHp=" + remainingPlayerHp
                + ", ammoUsed=" + ammoUsed
                + ", bombsUsed=" + bombsUsed
                + ", medkitsUsed=" + medkitsUsed
                + ", serumsUsed=" + serumsUsed + "}";
    }
}