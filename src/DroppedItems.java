
public class DroppedItems {	
	public Inventory inventory;
	public Position position;
	
	private DroppedItems(){
		
	}
	
	public DroppedItems(Item item, int count,int x, int y) {
		this.position = new Position(x,y);
		this.inventory = new Inventory();
		inventory.addItemStack(new ItemStack(item,count));
	}

	public DroppedItems(Inventory inventory, Position position) {
		this.inventory = inventory;
		this.position = new Position(position);
	}
}
