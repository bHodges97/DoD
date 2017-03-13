import java.awt.Image;

/**
 * Floor tile
 *
 */
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

	@Override
	protected TileType getTileType() {
		return TileType.TILEFLOOR;
	}

	@Override
	protected void onSteppedOn(Player player) {
	}

	@Override
	protected float getHeight() {
		return 0.01f;
	}

	@Override
	public Image[] getImage() {
		return Sprite.getRow(2);
	}
}
