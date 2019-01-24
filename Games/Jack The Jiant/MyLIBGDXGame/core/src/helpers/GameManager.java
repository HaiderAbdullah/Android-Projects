package helpers;

public class GameManager {
    private static final GameManager ourInstance = new GameManager();

    public boolean gameStartedFromMainMenu, isPaused = true;
    public int lifeScore, coinScore, score;

    private GameManager() {


    }

    public static GameManager getInstance() {
        return ourInstance;
    }


} // Game Manager
