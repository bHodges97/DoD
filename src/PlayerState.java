
public enum PlayerState implements Messageable {
	PLAYING,DEAD,STUNNED,ESCAPED;

	@Override
	public String getInfo() {
		return "<PLAYERSTATE>"+"</PLAYERSTATE>";
	}
}
