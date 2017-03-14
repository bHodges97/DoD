import java.awt.Image;

public class ItemStack implements Messageable,Displayable{
	private Item item;
	public int count;
	public ItemStack(Item item){
		this.item = item;
	}
	
	/**
	 * Constructor
	 * @param item Item type
	 * @param count Item count
	 */
	public ItemStack(Item item,int count){
		this.item = item;
		this.count = count;
	}
	
	/**
	 * @param count Return count
	 */
	public void add(int count){
		this.count+=count;
	}
	
	/**
	 * @return return name of item
	 */
	public String getName() {
		return item.getName();
	}

	@Override
	public char getDisplayChar(){
		return item.getDisplayChar();
	}
	
	@Override
	public String getInfo() {
		return "<ITEMSTACK>"+item.getInfo()+"<COUNT>"+count+"</COUNT></ITEMSTACK>";
	}

	@Override
	public Image[] getImages() {
		return item.getImages();
	}
}
