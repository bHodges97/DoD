
public class ItemStack {
	private Item item;
	public int count;

	private ItemStack(){
		
	}
	public ItemStack(Item item){
		this.item = item;
	}
	public ItemStack(Item item,int count){
		this.item = item;
		this.count = count;
	}
	public char getDisplayChar(){
		return item.getDisplayChar();
	}
}
