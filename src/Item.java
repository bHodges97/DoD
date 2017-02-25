import java.awt.Image;

public abstract class Item implements Messageable,Displayable{


	@Override
	public String getSummary() {
		return getName();
	}

	@Override
	public String getFullInfo() {
		return getName()+","+getDescription();
	}

	public abstract String getName();
	public abstract String getDescription();
}
