/**
 * used to select human actions
 *
 */
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
        //clear input and return;
        String holder = input;
        input = "";
        return holder;
	}  
}
