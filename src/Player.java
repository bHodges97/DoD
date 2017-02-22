/**
 * Abstract player class
 */
abstract class Player extends Thing{
	protected GameLogic gameLogic = null;
	protected int goldCount = 0;
	protected boolean isMainPlayer = false;
	protected int lives = 1;
	protected String name = "PLAYER";
	protected int id;
	protected Position position;
	
	
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
	protected String processCommand(String command){
		String output = "Invalid";
		if(command.equals("HELLO")){
			output = gameLogic.hello();
		}else if(command.equals("PICKUP")){
			output = gameLogic.pickup(this);
		}else if(command.equals("LOOK")){
			output = gameLogic.look(this);
		}else if(command.equals("QUIT")){
			gameLogic.quitGame();
		}else if(command.length() == 6 && command.substring(0,5).equals("MOVE ")){
			output = gameLogic.move(this,command.charAt(5));
	    }
	    return output;
	}
	protected abstract void selectNextAction();
	protected abstract boolean isImmortal();
}
