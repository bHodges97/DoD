import java.io.IOException;

public class ControllerHuman extends Controller{
	private MyFrame gui;
	
	public ControllerHuman(MyFrame gui){
		//this.player = player;
		this.gui = gui;
	}
	
	@Override
	public String getInput() {
		try {
			return gui.console.readln();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void sendOutput(String output) {
		gui.console.println(output);
	}    
    

}
