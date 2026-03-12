package mazegame;

public class SaveData {
    public int mazeLevel;
    public int coins;
    public int lanternLevel;
    public int speedLevel;
    public int healthLevel;
    public int bagLevel;
    public int pickaxes;
    public double timePlayed;

    // Default constructor for Jackson
    public SaveData() {}

    public SaveData(Game game) {
        Player p = game.getPlayer();
        this.mazeLevel = game.getMaze().getMazeLevel();
        this.coins = p.getCoins();
        this.lanternLevel = p.getLanternLevel();
        this.speedLevel = p.getSpeedLevel();
        this.healthLevel = p.getMaxHealthLevel();
        this.bagLevel = p.getBagLevel();
        this.pickaxes = p.getPickaxes();
        this.timePlayed = game.getTimePlayed();
    }
}
