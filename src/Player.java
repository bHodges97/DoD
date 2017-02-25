/**
 * Abstract player class
 */
abstract class Player extends Thing{
	protected GameLogic gameLogic = null;
	protected int lives = 1;
	protected String name = "PLAYER";
	protected int id;
	protected Position position;
	protected Inventory inventory = new Inventory();
	protected Controller controller;
	protected PlayerState state;
	
	public String getSummaryShort(){
		return name+","+id+","+getGoldCount();
	}
	public String getSummaryLong(){
		return getSummaryShort()+","+position+inventory.toString();
	}
	
	
	/**
	 * @param logic Set the gamelogic this player belongs to.
	 */
    protected void setGameLogic(GameLogic logic){
    	this.gameLogic = logic;
    }
    /**
     * @return Player gold count
     */
    protected int getGoldCount(){
    	return inventory.getItemCount("Gold");
    }
    /**
     * Add i amount of gold to player
     * @param i the amount of gold to add
     */
    protected void addGold(int i){
    	inventory.getItemStack("Gold").add(i);
    }
	protected String processCommand(String command){
		String output = "Invalid";
		if(command.equals("HELLO")){
			output = gameLogic.hello();
		}else if(command.equals("PICKUP")){
			output = gameLogic.pickup(this);
		}else if(command.equals("LOOK")){
			output = gameLogic.look(this);
		}else if(command.equals("QUIT")){
			gameLogic.quitGame();
		}else if(command.length() == 6 && command.substring(0,5).equals("MOVE ")){
			output = gameLogic.move(this,command.charAt(5));
	    }
	    return output;
	}
	 
    /**
     * Uses getInputFromConsole() to read from console, processCommand() to process the reading,
     * and then displays in console the final answer.
     */
    protected void selectNextAction() {
    	String input,output = "Invalid";
    	//keep getting inputs until one is valid
    	do{
    		input = controller.getInput();
    		controller.sendOutput(processCommand(input));
    	}while((output.equals("Invalid") || output.equals("Fail"))||(input.equals("LOOK"))||input.equals("HELLO"));
    } 
    
	protected abstract boolean isImmortal();
	public void tryEscape() {
		int goldCount = Integer.valueOf(gameLogic.hello().split("HELLO :")[0]);
		if(this.getGoldCount() >= goldCount){
			this.state = PlayerState.ESCAPED;
		}
	}
	
	
}
