/**
 * 
 * Abstract Item, currently only Gold exists
 */
public abstract class Item implements Messageable,Displayable{

	public static Item createItem(String name){
		if(name.equals(new ItemGold().getName())){
			return new ItemGold();
		}
		return null;
	}
	
	@Override
	public String getInfo() {
		return "<ITEM><NAME>"+getName()+"</NAME></ITEM>";
	}
	
	/**
	 * @return Item name
	 */
	public abstract String getName();
	/**
	 * @return Item description
	 */
	public abstract String getDescription();
}
