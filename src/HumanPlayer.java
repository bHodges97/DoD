/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 * @author : The underwhelmed tutor.
 */
public class HumanPlayer extends Player{	

	private static final char displayChar = 'P';
	
	
	public HumanPlayer(GameLogic gameLogic, Controller controller){
		this.gameLogic = gameLogic;
		this.controller = controller;
		controller.player = this;
	}
	
	@Override
	public char getDisplayChar() {
		return displayChar;
	}
	
	@Override
	protected boolean isImmortal(){
		return false;
	}

}
