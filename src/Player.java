/**
 * Abstract player class
 */
abstract class Player{
	protected GameLogic gameLogic = null;
	protected int goldCount = 0;
	protected boolean isMainPlayer = false;
	protected int lives = 1;
	protected int strength = 0;
	protected String name = "PLAYER";
	
	/**
	 * @param logic Set the gamelogic this player belongs to.
	 */
    protected void setGameLogic(GameLogic logic){
    	this.gameLogic = logic;
    }
    /**
     * @return Player gold count
     */
    protected int getGoldCount(){
    	return goldCount;
    }
    /**
     * Add i amount of gold to player
     * @param i the amount of gold to add
     */
    protected void addGold(int i){
    	goldCount+=i;
    }
	protected abstract String processCommand(String command);
	protected abstract void selectNextAction();
}
