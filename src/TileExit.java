

public class TileExit extends Tile {
	protected static final char displayChar = 'E';
	protected static final boolean passable = true;
	
	protected TileExit(Position pos){
		super(pos);
	}

	@Override
	public char getDisplayChar() {
		return displayChar;
	}

	@Override
	protected boolean isPassable() {
		return passable;
	}
}