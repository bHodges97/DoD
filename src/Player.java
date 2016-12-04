import java.lang.reflect.Method;

abstract class Player{
	protected GameLogic gameLogic = null;
	protected int goldCount = 0;

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
}
