
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
	
	
	public String getInfo(){
		return "<TILE><CHAR>"+getDisplayChar()+"</CHAR>"+pos.getInfo()+"</TILE>";
	}
}
