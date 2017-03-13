import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is used to store player gold and future items to add to the game
 *
 */
public class Inventory implements Messageable,Displayable{
	private List<ItemStack> itemStacks;
	
	public Inventory(){
		itemStacks = new ArrayList<ItemStack>();
	}
	
	/**
	 * @param itemStack To add to inventory
	 */
	public void addItemStack(ItemStack itemStack) {
		itemStacks.add(itemStack);
	}

	/**
	 * @param itemStack The item stack to remove
	 * @return true if the itemstack can be found
	 */
	public boolean removeStack(ItemStack itemStack) {
		if(itemStacks.contains(itemStack)){
			itemStacks.remove(itemStack);
			return true;
		}
		return false;		
	}
	
	/**
	 * @return true if the inventory is input
	 */
	public boolean isEmpty(){
		for(ItemStack stack:itemStacks){
			if(stack.count > 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Clear the stored contents
	 */
	public void empty(){
		itemStacks = new ArrayList<ItemStack>();
	}

	/**
	 * 
	 * @param name the name of item
	 * @return  count of given item
	 */
	public int getItemCount(String name) {
		if(getItemStack(name) == null){
			return 0;
		}
		return getItemStack(name).count;
	}
	
	/**
	 * @param name
	 * @return The first itemstack with the given name
	 */
	public ItemStack getItemStack(String name) {
		for(ItemStack stack:itemStacks){
			if(stack.getName() == name){
				return stack;
			}
		}
		return null;
	}
	
	/**
	 * Transfer contents from inventory to another
	 * @param from The inventory to transfer from
	 * @param to The inventory to transfer to
	 */
	public static void transfer(Inventory from, Inventory to) {
		//toBeDeleted is a holder to avoid ConcurrentModificationException when iterating
		Set<ItemStack> toBeDeleted = new HashSet<ItemStack>();
		for(ItemStack stackFrom:from.itemStacks){
			boolean stackExists = false;
			for(ItemStack stackTo:to.itemStacks){
				if(stackFrom.getName().equals(stackTo.getName())){
					stackTo.count+=stackFrom.count;
					toBeDeleted.add(stackFrom);
					stackExists = true;
					break;
				}
			}
			if(!stackExists){
				to.addItemStack(stackFrom);
				toBeDeleted.add(stackFrom);
			}
		}
		for(ItemStack stack:toBeDeleted){
			from.removeStack(stack);
		}
		
	}

	@Override
	public char getDisplayChar(){
		return itemStacks.get(0).getDisplayChar();
	}

	@Override
	public String getInfo() {
		String builder = "<INVENTORY>";
		for(ItemStack stack:itemStacks){
			builder+=stack.getInfo();
		}
		builder+="</INVENTORY>";
		return builder;
	}

	@Override
	public Image[] getImage() {
		return itemStacks.get(0).getImage();
	}
	
}
