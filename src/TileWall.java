import java.awt.Image;

/**
 * Wall tile
 */
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

	@Override
	protected void onSteppedOn(Player player) {
		throw new IllegalArgumentException("PLAYER ON WALL TILE");
	}

	@Override
	protected float getHeight() {
		return 1;
	}

	@Override
	public Image[] getImages() {
		return Sprite.getRow(3);
	}
	
}
