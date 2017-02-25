
public class ControllerBot extends Controller{

	private String output = "START";
	

	@Override
	public String getInput() {
		return "PICKUP";
	}

	@Override
	public void sendOutput(String output) {
		this.output = output;
	}
}
