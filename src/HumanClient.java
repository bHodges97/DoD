
public class HumanClient extends Client{
	public static void main(String[] args) {
		HumanClient client = new HumanClient();
		if(!client.validateParams(args)){
			return;
		}
		if(!client.tryConnect(args[0],client.port)){
			System.out.println("Failed to connect to "+args[0]+" : "+args[1]);
			return;
		}
		
	}
	
	
	
	
}
