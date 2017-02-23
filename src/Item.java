import java.awt.Image;

public abstract class Item extends Thing{


	@Override
	public String getSummaryShort() {
		return getName();
	}

	@Override
	public String getSummaryLong() {
		return getName()+","+getDescription();
	}

	public abstract String getName();
	public abstract String getDescription();
}
