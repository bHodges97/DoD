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
}
