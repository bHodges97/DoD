/**
 * Class for items that on the dungeon floor
 */
public class DroppedItems implements Messageable{	
	public Inventory inventory;
	public Position position;
	
	/**
	 * Constructor
	 * @param item Item type
	 * @param count Item count
	 * @param x dungeon position x
	 * @param y dungeon position y
	 */
	public DroppedItems(Item item, int count,int x, int y) {
		this.position = new Position(x,y);
		this.inventory = new Inventory();
		inventory.addItemStack(new ItemStack(item,count));
	}
	
	/**
	 * Constructor with premade inventory
	 * @param inventory Inventory to drop at location
	 * @param position The position to drop at
	 */
	public DroppedItems(Inventory inventory, Position position) {
		this.inventory = inventory;
		this.position = new Position(position);
	}

	@Override
	public String getInfo() {
		return "<DROPPEDITEMS>"+inventory.getInfo()+position.getInfo()+"</DROPPEDITEMS>";
	}
}
