
public class ControllerBot extends Controller{

	private String output = "START";
	

	public ControllerBot(DODServer dodServer, int id,Player player) {
		super(dodServer,id,player);
	}

	@Override
	public String getInput() {
		return "PICKUP";
	}

	@Override
	public void sendOutput(String output) {
		this.output = output;
	}
}
