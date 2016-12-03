abstract class Player{
	private GameLogic gameLogic;
	
	protected String processCommand(String command) {
		String output = "";
		if(command.equals("HELLO")){
			output = gameLogic.hello();
		}else if(command.equals("PICKUP")){
			output = gameLogic.pickup();
		}else if(command.equals("LOOK")){
			output = gameLogic.look();
		}else if(command.equals("QUIT")){
			gameLogic.quitGame();
		}else if(command.length() == 6 && command.substring(0,5).equals("MOVE ")){
			output = gameLogic.move(command.charAt(5));
	    }else{
	    	output = "Invalid";
	    }
	    return output;
    }
}
