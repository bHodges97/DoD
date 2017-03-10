/**
 * Abstract class for implementing different tile types
 * @author ben
 *
 */
public abstract class Tile implements Displayable,Messageable {
	public enum TileType{TILEFLOOR,TILEWALL,TILEEXIT};
	protected final Position pos;
	
	/**
	 * Construct a new tile in the given position
	 * @param pos the position of the tile
	 */
	protected Tile(Position pos){
		this.pos = pos;
	}
	
	/**
	 * Create a tile base on given tile type
	 * @param type 
	 * @param position The position of the new tile
	 * @return new tile of given type
	 */
	protected static Tile makeTile(TileType type, Position position){
		switch(type){
		case TILEEXIT:
			return new TileExit(position);
		case TILEWALL:
			return new TileWall(position);
		case TILEFLOOR:
			return new TileFloor(position);
		default:
			throw new IllegalArgumentException("Type error");
		}
	}
	/**
	 * @return true if player can walk over tile
	 */
	protected abstract boolean isPassable();
	/**
	 * @return type of tile
	 */
	protected abstract TileType getTileType();	
	/**
	 * Action when player stands on tile
	 * @param player the player that triggered this tile
	 */
	protected abstract void onSteppedOn(Player player);
	
	protected abstract float getHeight();
	
	@Override
	public String toString(){
		return getDisplayChar()+pos.toString();
	}
	
	@Override
	public String getInfo(){
		return "<TILE><TYPE>"+getTileType()+"</TYPE>"+pos.getInfo()+"</TILE>";
	}
	
}
