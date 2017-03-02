public abstract class Item implements Messageable,Displayable{



	@Override
	public String getInfo() {
		return "<ITEM><NAME>"+getName()+"</NAME></ITEM>";
	}

	public abstract String getName();
	public abstract String getDescription();
}
