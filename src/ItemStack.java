
public class ItemStack implements Messageable{
	private Item item;
	public int count;
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
	
	public void add(int count){
		this.count+=count;
	}
	public String getName() {
		return item.getName();
	}
	@Override
	public String getInfo() {
		return "<ITEMSTACK>"+item.getInfo()+"<COUNT>"+count+"</COUNT></ITEMSTACK>";
	}
}
