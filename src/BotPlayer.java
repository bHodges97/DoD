import java.awt.Image;

/**
 * Starts the game with a Bot Player. Contains code for bot's decision making.
 *
 * @author : The unhelmed tutor.
 */
public class BotPlayer extends Player{
	private static final char displayChar = 'B';

    @Override
	public char getDisplayChar() {
		return displayChar;
	}
    
    @Override
	protected boolean isImmortal(){
		return true;
	}

	@Override
	protected PlayerType getPlayerType() {
		return PlayerType.BOTPLAYER;
	}

	@Override
	public Image getImage(int type) {
		 if(state == PlayerState.ESCAPED || state == PlayerState.DEAD){
			return Sprite.getSprite(4, 9);
		}
		return Sprite.getSprite(type,0);
	}

}
