
public class ControllerHuman extends Controller{
	
	public ControllerHuman(DODServer dodServer, int id,Player player) {
		super(dodServer,id,player);
	}

	@Override
	public synchronized String getInput() {

        while(input.isEmpty()) {
            try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        String holder = input;
        input = "";
        return holder;
	}

	@Override
	public void sendOutput(String output) {
		output = Parser.sanitise(output);
		server.processInput("<OUTPUT>"+ output + "</OUTPUT>", id);
	}

	@Override
	public void processInfo(String message) {
		// TODO Auto-generated method stub
		
	}   
}
