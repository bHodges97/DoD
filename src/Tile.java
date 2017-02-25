
public abstract class Tile extends Thing {
	protected final Position pos;
	
	private Tile(){
		pos = null;
	}
	
	protected Tile(Position pos){
		this.pos = pos;
	}

	protected abstract boolean isPassable();
	
	public void onSteppedOn(Player player){};
	
	public String toString(){
		return getDisplayChar()+pos.toString();
	}
	

	public String getSummaryShort(){
		return toString();
	}
	public String getSummaryLong(){
		return toString()+","+isPassable();
	}
}
