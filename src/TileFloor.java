

public class TileFloor extends Tile {

	protected static final char displayChar = '.';
	protected static final boolean passable = true;
	
	protected TileFloor(Position pos){
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
