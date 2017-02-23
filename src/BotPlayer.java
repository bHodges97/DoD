/**
 * Starts the game with a Bot Player. Contains code for bot's decision making.
 *
 * @author : The unhelmed tutor.
 */
public class BotPlayer extends Player{
	private static final char displayChar = 'B';
	
	public BotPlayer(GameLogic logic){
		name = "bot";
	}

    /**
     * Selects the next action the bot will perform. Outputs in gameLogic.getConsole() the final result.
     */
    public void selectNextAction() {
    	String output = processCommand("LOOK");
    	
    }	
    
    @Override
	public char getDisplayChar() {
		return displayChar;
	}
    
    @Override
	protected boolean isImmortal(){
		return true;
	}

}
