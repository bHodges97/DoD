import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Inventory implements Messageable{
	private List<ItemStack> itemStacks;
	
	public Inventory(){
		itemStacks = new ArrayList<ItemStack>();
	}
	
	public char getDisplayChar(){
		return itemStacks.get(0).getDisplayChar();
	}

	public void addItemStack(ItemStack itemStack) {
		itemStacks.add(itemStack);
	}

	private boolean removeStack(ItemStack itemStack) {
		if(itemStacks.contains(itemStack)){
			itemStacks.remove(itemStack);
			return true;
		}
		return false;		
	}

	public int getItemCount(String name) {
		if(getItemStack(name) == null){
			return 0;
		}
		return getItemStack(name).count;
	}

	public ItemStack getItemStack(String name) {
		for(ItemStack stack:itemStacks){
			if(stack.getName() == name){
				return stack;
			}
		}
		return null;
	}

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
	public String getInfo() {
		String builder = "<INVENTORY>";
		for(ItemStack stack:itemStacks){
			builder+=stack.getInfo();
		}
		builder+="</INVENTORY>";
		return builder;
	}
	
	public boolean isEmpty(){
		for(ItemStack stack:itemStacks){
			if(stack.count > 0){
				return false;
			}
		}
		return true;
	}
	
	public void empty(){
		itemStacks = new ArrayList<ItemStack>();
	}
}
