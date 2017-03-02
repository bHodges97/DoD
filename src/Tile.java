
public abstract class Tile implements Displayable,Messageable {
	public enum TileType{TILEFLOOR,TILEWALL,TILEEXIT};
	protected final Position pos;
	
	protected Tile(Position pos){
		this.pos = pos;
	}
	
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

	protected abstract boolean isPassable();
	protected abstract TileType getTileType();	
	public void onSteppedOn(Player player){};
	
	public String toString(){
		return getDisplayChar()+pos.toString();
	}
	
	
	public String getInfo(){
		return "<TILE><TYPE>"+getTileType()+"</TYPE>"+pos.getInfo()+"</TILE>";
	}
}
