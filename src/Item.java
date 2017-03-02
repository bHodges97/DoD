public abstract class Item implements Messageable,Displayable{



	@Override
	public String getInfo() {
		return "<ITEM><NAME>"+getName()+"</NAME><DESCRIPTION>"+getDescription()+"</DESCRIPTION></ITEM>";
	}

	public abstract String getName();
	public abstract String getDescription();
}
