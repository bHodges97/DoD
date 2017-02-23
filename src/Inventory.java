import java.util.ArrayList;
import java.util.List;

public class Inventory {
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
		for(ItemStack stackFrom:from.itemStacks){
			for(ItemStack stackTo:to.itemStacks){
				if(stackFrom.getName().equals(stackTo.getName())){
					stackTo.count+=stackFrom.count;
					from.removeStack(stackFrom);
					break;
				}
			}
		}
		for(ItemStack stackFrom:from.itemStacks){
			to.addItemStack(stackFrom);
			from.removeStack(stackFrom);
		}
		
	}
}
