import java.net.Socket;

public class Client {
	
	int port = -1;
 	Thread sendThread;
	Thread receiveThread;
	boolean clientReady = false;
	
	protected boolean validateParams(String[] args){ 
		if(args.length == 2){	
			try{
				port = Integer.valueOf(args[1]);
				if(port < 0 || port > 65535){
					throw new NumberFormatException();
				}
			}catch (NumberFormatException e) {
				System.out.println(args[1]+ " is not a valid port number.");
				return false;
			}
			
		}else{
			System.out.println("Expected parameters: hostname portnumber");
			return false;
		}
		return true;
	}
	
	
	boolean tryConnect(String hostName, int portNumber){
		try {
			Socket sock = new Socket(hostName,portNumber);
			if(!sock.isConnected()){
				return false;
			}
			sendThread = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
				}
			};
			
			receiveThread =new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
				}
			};
			sendThread.start();
			receiveThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return true;
	}


	public void processInput(String line) {
		
		
	}
}
