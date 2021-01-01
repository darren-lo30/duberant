package duber.game;


import duber.engine.GameEngine;
import duber.engine.IGameLogic;

public class Main {
    public static void main(String[] args) {
        try {
            IGameLogic gameLogic = new Duberant();
            GameEngine gameEngine = new GameEngine("Duberant", 1000, 1000, true, gameLogic);
            gameEngine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}