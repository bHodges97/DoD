import java.awt.Image;
import java.util.Random;

/**
 * Floor tile
 *
 */
public class TileFloor extends Tile {

	protected static final char displayChar = '.';
	protected static final boolean passable = true;
	private int style = 0;
	
	protected TileFloor(Position pos){
		super(pos);
		style = new Random().nextInt(4);
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
	public Image getImage(int type) {
		return Sprite.getSprite(style,2);
	}
}
