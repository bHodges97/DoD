
public class BotClient extends Client {
	
	
	
	public static void main(String[] args) {
		BotClient client = new BotClient();
		if(args.length == 2){
			if(!client.tryConnect(args[0],args[1])){
				System.out.println("Failed to connect to "+args[0]+" : "+args[1]);
				return;
			}
		}else{
			System.out.println("how do I phrase this");
		}
	}

	
}
