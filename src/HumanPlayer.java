import java.io.IOException;

/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 * @author : The underwhelmed tutor.
 */
public class HumanPlayer extends Player{	
	
	public HumanPlayer(GameLogic gameLogic){
		isPlayer= true;
		this.gameLogic = gameLogic;
	}
	
    /**
    * Reads player's to the console.
    * <p>
    * @return : A string containing the input the player entered.
    */
    protected String getInputFromConsole() {
		try {
			return gameLogic.console.readln();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
    }

    /**
     * Processes the command. It should return a reply in form of a String, as the protocol dictates.
     * Otherwise it should return the string "Invalid".
     *
     * @param command : Input entered by the user.
     * @return : Processed output or Invalid if the @param command is wrong.
     */
	protected String processCommand(String command) {
		String output = "Invalid";
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
    		gameLogic.console.println(output);
    		output = processCommand(getInputFromConsole());
    	}
    	gameLogic.console.println(output);
    }    

    public static void main(String[] args) {
    	Console console = new Console();
    	GameLogic game = new GameLogic(console);
		game.addPlayer(new HumanPlayer(game));
		game.addPlayer(new BotPlayer(game));
        // RUN FOREST RUN!
		game.startGame();
    }
}
