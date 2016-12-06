abstract class Player{
	protected GameLogic gameLogic = null;
	protected int goldCount = 0;
	protected boolean isMainPlayer = false;
	protected boolean alive = true;

    protected void setGameLogic(GameLogic logic){
    	this.gameLogic = logic;
    }
    protected int getGoldCount(){
    	return goldCount;
    }
    protected void addGold(int i){
    	goldCount+=i;
    }
	
    protected boolean isPlayer(){
    	return isMainPlayer;
    }
	protected abstract String processCommand(String command);
	protected abstract void selectNextAction();
}
