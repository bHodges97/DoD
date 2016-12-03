/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 * @author : The underwhelmed tutor.
 */
public class HumanPlayer extends Player{
	
	private int goldCount = 0;
	private GameLogic gameLogic;
	
	public HumanPlayer(GameLogic gameLogic){
		this.gameLogic = gameLogic;
	}
	
    /**
    * Reads player's to the console.
    * <p>
    * @return : A string containing the input the player entered.
    */
    protected String getInputFromConsole() {
		return gameLogic.getConsole().readln();
    }

    /**
     * Processes the command. It should return a reply in form of a String, as the protocol dictates.
     * Otherwise it should return the string "Invalid".
     *
     * @param command : Input entered by the user.
     * @return : Processed output or Invalid if the @param command is wrong.
     */
	protected String processCommand(String command) {
		String output = "";
		if(command.equals("HELLO")){
			output = gameLogic.hello();
		}else if(command.equals("PICKUP")){
			output = gameLogic.pickup();
		}else if(command.equals("LOOK")){
			output = gameLogic.look();
		}else if(command.equals("QUIT")){
			gameLogic.quitGame();
		}else if(command.length() == 6 && command.substring(0,5).equals("MOVE ")){
			output = gameLogic.move(command.charAt(5));
	    }else{
	    	output = "Invalid";
	    }
	    return output;
    }
    /**
     * Uses getInputFromConsole() to read from console, processCommand() to process the reading,
     * and then displays in console the final answer.
     */
    protected void selectNextAction() {
    	String input = getInputFromConsole();
    	String output = processCommand(input);
    	while((output.equals("Invalid") || output.equals("Fail"))||(input.equals("LOOK"))){
    		gameLogic.getConsole().println(output);
    		output = processCommand(getInputFromConsole());
    	}
    	gameLogic.getConsole().println(output);
    }
    
    protected int getGoldCount(){
    	return goldCount;
    }
    
    protected void addGold(int count){
    	goldCount+=count;
    }
    
    protected void setGameLogic(GameLogic logic){
    	this.gameLogic = logic;
    }

    public static void main(String[] args) {
    	Console console = new Console();
    	GameLogic main = new GameLogic(console);    	
        // RUN FOREST RUN!
    }
}
