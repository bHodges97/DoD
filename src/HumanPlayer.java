import java.io.IOException;

/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 * @author : The underwhelmed tutor.
 */
public class HumanPlayer extends Player{	

	private static final char displayChar = 'P';
	
	
	public HumanPlayer(GameLogic gameLogic){
		this.gameLogic = gameLogic;
	}
	
	@Override
	public char getDisplayChar() {
		return displayChar;
	}
	
	@Override
	protected boolean isImmortal(){
		return false;
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
			e.printStackTrace();
		}
		return "";
    }
      
    
    public static void main(String[] args) {
    	MyFrame gui;
    	if(args.length > 0){
    		gui = new MyFrame("Dungeon of Doom",!args[0].equals("TEXTONLY"));
    	}else{
    		gui = new MyFrame("Dungeon of Doom",true);
    	}
    	GameLogic game = new GameLogic(gui);
		game.addPlayer(new HumanPlayer(game));

		game.addPlayer(new HumanPlayer(game));
		game.addPlayer(new BotPlayer(game));
        // RUN FOREST RUN!
		game.startGame();
    }

}
