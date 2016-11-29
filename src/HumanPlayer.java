import java.util.Scanner;

/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 * @author : The underwhelmed tutor.
 */
public class HumanPlayer {
	
	private int goldCount = 0;
	private GameLogic gameLogic;
	Scanner scanner = new Scanner(System.in);
	
	public HumanPlayer(GameLogic gameLogic){
		this.gameLogic = gameLogic;
	}
	
    /**
    * Reads player's to the console.
    * <p>
    * @return : A string containing the input the player entered.
    */
    protected String getInputFromConsole() {
		//TODO: swap with console;
		String input = "";
		while(true){
			if(scanner.hasNextLine()){
				input = scanner.nextLine();
				if(!input.isEmpty()){
					return input;
				}
			}
		}
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
		}else if(command.length() == 5 && command.substring(0,4).equals("MOVE")){
			output = gameLogic.move(command.charAt(4));
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
    	Console.println(processCommand(getInputFromConsole()));
    }
    
    public int getGoldCount(){
    	return goldCount;
    }

    public static void main(String[] args) {
    	GameLogic main = new GameLogic();
        // RUN FOREST RUN!
    }
}
