import java.awt.Image;

/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 * @author : The underwhelmed tutor.
 */
public class HumanPlayer extends Player{	

	private static final char displayChar = 'P';
	
	
	@Override
	public char getDisplayChar() {
		return displayChar;
	}
	
	@Override
	protected boolean isImmortal(){
		return false;
	}

	@Override
	protected PlayerType getPlayerType() {
		return PlayerType.HUMANPLAYER;
	}

	@Override
	public Image getImage(int param) {
		return Sprite.getSprite(param,1);
	}
}
