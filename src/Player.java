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
	protected abstract String processCommand(String command);
	protected abstract void selectNextAction();
	
	
	//TODOLIST
	//TODO:path finding may be broken?
	//TODO:win/lose animation
	//TODO:make a map reader class
	//TODO:Fix issues with resizing
	//TODO: make big classes smaller
	//TODO:CARET POS
}
