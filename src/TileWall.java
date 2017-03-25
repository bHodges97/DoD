import java.awt.Image;
import java.util.Random;

/**
 * Wall tile
 */
public class TileWall extends Tile {
	public static final char displayChar = '#';
	public static final boolean passable = false;
	private int style = 0;	
	
	public TileWall(Position pos){
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
	public Image getImage(int type) {
		return Sprite.getSprite(style,3);
	}
	
}
