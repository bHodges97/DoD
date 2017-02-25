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

}
