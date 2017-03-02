

public class TileWall extends Tile {
	public static final char displayChar = '#';
	public static final boolean passable = false;
	
	
	
	public TileWall(Position pos){
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

	@Override
	protected TileType getTileType() {
		return TileType.TILEWALL;
	}
	
}
