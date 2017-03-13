import java.awt.Image;

/**
 * Gold
 *
 */
public class ItemGold extends Item {
	private final static String name = "Gold";
	private final static String description = "Used to exit the dungeon.";

	@Override
	public char getDisplayChar() {
		return 'G';
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Image[] getImage() {
		return Sprite.getRow(5);
	}

}
