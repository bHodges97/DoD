

import java.awt.Color;
import java.awt.Image;
import java.util.HashSet;
import java.util.Set;

/**
 * Message element
 * Consists of tag, children and value
 *
 */
public class Element implements Messageable{
	public Element(String name) {
		this.tag = name;
	}
	public String tag = "";
	public Set<Element> children = new HashSet<Element>();
	public String value = "";
	
	/**
	 * Print the contents recursively
	 * @param depth The recursion depth
	 */
	public void print(int depth){
		for(int i = 0;i < depth;i++){
			System.out.print("--");
		}
		System.out.print(tag+"\n");
		if(children.isEmpty()){
			for(int i = 0;i < depth;i++){
				System.out.print("--");
			}
			System.out.print("--"+value+"\n");
			return;
		}else{
			for(Element e : children){
				e.print(depth+1);
			}
		}
	}
	
	/**
	 * @return true if the stored value is a boolean
	 */
	public boolean isBoolean(){
		return value.equals("true") || value.equals("false");
	}
	
	/**
	 * @return stored value as a boolean
	 */
	public boolean toBoolean() {
		return value.equals("true");
	}
	
	/**
	 * @return is stored value an integer
	 */
	public boolean isInt(){
		try{
			Integer.valueOf(value);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	/**
	 * @return stored value as an integer
	 */
	public int toInt(){
		return Integer.parseInt(value);
	}
	
	/**
	 * @return the stored value as a char
	 */
	public char toChar(){
		return value.toCharArray()[0];
	}

	/**
	 * @param player The player load the element attributes into
	 */
	public void toLobbyPlayer(LobbyPlayer player){
		for(Element child:children){
			String tag = child.tag;
			String value = child.value;
			if(tag.equals("READY")){
				if(child.isBoolean()){
					player.ready = child.toBoolean();
				}
			}else if(tag.equals("NAME")){
				player.name = value;
			}else if(tag.equals("COLOR")){
				if(child.isInt()){
					player.color = new Color(child.toInt());
				}
			}else if(tag.equals("BOT")){
				if(child.isBoolean()){
					player.isBot = child.toBoolean();
				}
			}else if(tag.equals("CONNECTED")){
				if(child.isBoolean()){
					player.connected = child.toBoolean();
				}
			}else if(tag.equals("ID")){
				if(child.isInt()){
					player.id = child.toInt();
				}
			}
		}	
	}
	
	/**
	 * @return The element as a position
	 */
	public Position toPosition(){
		int x = getChild("X").toInt();
		int y = getChild("Y").toInt();
		return new Position(x,y);
	}
	
	/**
	 * @return The element as a tile
	 */
	public Tile toTile(){
		Position pos = getChild("POSITION").toPosition();
		Tile.TileType type = Tile.TileType.valueOf(getChild("TYPE").value);
		return Tile.makeTile(type, pos);
	}
	
	/**
	 * @return The element as a player
	 */
	public Player toPlayer(){
		Player player = null;
		int id = getChild("ID").toInt();
		Position pos = getChild("POSITION").toPosition();
		Player.PlayerType type = Player.PlayerType.valueOf(getChild("TYPE").value);
		player = Player.makePlayer(type);
		player.id = id;
		player.position = pos;
		player.state = PlayerState.valueOf(getChild("PLAYERSTATE").value);
		player.inventory = getChild("INVENTORY").toInventory();
		return player;
	}
	
	public DroppedItems toDroppedItems(){

		Position pos = getChild("POSITION").toPosition();
		Inventory inv = getChild("INVENTORY").toInventory();
		return new DroppedItems(inv, pos);
	}
	
	public Inventory toInventory(){
		Inventory inventory = new Inventory();
		for(Element child:children){
			inventory.addItemStack(child.toItemStack());
		}
		return inventory;
	}
	
	public ItemStack toItemStack(){
		return new ItemStack(Item.createItem(getChild("ITEM").getChild("NAME").value), getChild("COUNT").toInt());
	}
	
	@Override
	public String getInfo() {
		String contents = value;
		for(Element child:children){
			contents+=child.getInfo();
		}
		return "<"+tag+">" +contents+ "</"+tag+">";
	}
	
	/**
	 * Get a named child element
	 * @param name The name of a child element
	 * @return The first child element with given name
	 */
	public Element getChild(String name){
		for(Element child:children){
			if(child.tag.equals(name)){
				return child;
			}
		}
		return null;
	}
	
	
}
