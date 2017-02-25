
public abstract class Tile implements Displayable,Messageable {
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
	

	public String getSummary(){
		return toString();
	}
	public String getFullInfo(){
		return toString()+","+isPassable();
	}
}
