/**
 * 
 * Abstract Item, currently only Gold exists
 */
public abstract class Item implements Messageable,Displayable{

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
