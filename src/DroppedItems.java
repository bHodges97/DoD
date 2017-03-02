
public class DroppedItems implements Messageable{	
	public Inventory inventory;
	public Position position;
	
	public DroppedItems(Item item, int count,int x, int y) {
		this.position = new Position(x,y);
		this.inventory = new Inventory();
		inventory.addItemStack(new ItemStack(item,count));
	}

	public DroppedItems(Inventory inventory, Position position) {
		this.inventory = inventory;
		this.position = new Position(position);
	}

	@Override
	public String getInfo() {
		return "<DROPPEDITEMS>"+inventory.getInfo()+position.getInfo()+"</DROPPEDITEMS>";
	}
}
