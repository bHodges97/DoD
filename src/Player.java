import java.awt.Color;

/**
 * Abstract player class
 */
abstract class Player implements Messageable,Displayable{
	public enum PlayerType{HUMANPLAYER,BOTPLAYER};
	protected GameLogic gameLogic = null;
	protected int lives = 1;
	protected String name = "PLAYER";
	protected int id;
	protected Position position;
	protected Inventory inventory = new Inventory();
	protected Controller controller;
	protected PlayerState state;
	protected Color color;
	protected FightResolver fightResolver = null;
	
	/**
	 * @return true if player can't be attacked
	 */
	protected abstract boolean isImmortal();
	/**
	 * @return player type
	 */
	protected abstract PlayerType getPlayerType();
	/**
	 * Construct a player based on the given type
	 * @param playerType
	 * @return the new player
	 */
	protected static Player makePlayer(PlayerType playerType){
		switch(playerType){
		case BOTPLAYER:
			return new BotPlayer();
		case HUMANPLAYER:
			return new HumanPlayer();
		default:
			return null;
		}		
	}
	@Override
	public String getInfo(){
		return "<PLAYER><TYPE>"+getPlayerType()+"</TYPE><NAME>"+name+"</NAME><ID>"+id+"</ID>"+position.getInfo()+inventory.getInfo()+state.getInfo()+"</PLAYER>";
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
    
    /**
     * Process player commands, Valid commands are HELLO PICKUP LOOK and MOVE
     * @param command Command to process
     * @return Output of command. "Invalid" if command is not valid.
     */
	protected String processCommand(String command){
		if(state == PlayerState.ESCAPED){
			return "You have escaped type QUIT to stop playing";
		}else if(state == PlayerState.DEAD){
			return "You are dead type QUIT to stop playing";
		}else if(state == PlayerState.STUNNED){
			//TODO:unimplemented
			return "You are stunned";
		}		
		String output = "Invalid";
		if(fightResolver != null){
			return fightResolver.handle(command,this);
		}
		
		
		if(command.equals("HELLO")){
			output = gameLogic.hello();
		}else if(command.equals("PICKUP")){
			output = gameLogic.pickup(this);
		}else if(command.equals("LOOK")){
			output = gameLogic.look(this);
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
    	String input = controller.getInput();
    	controller.sendOutput(processCommand(input));
    } 
    
    /**
     * Check if player can win  and then make player win.
     */
	public void tryEscape() {
		int goldCount = Integer.valueOf(processCommand("HELLO").split("GOLD: ")[1]);
		if(this.getGoldCount() >= goldCount){
			this.state = PlayerState.ESCAPED;
			controller.sendOutput("ESCAPED");
		}
		gameLogic.informPlayers("Player "+id+ " has escaped. There is likely not enough gold left on the map, remaining players are stuck for ever");
	}
	
	/**
	 * Check if player is still in the game map 
	 * 
	 * @return <b>true</b> if player is still in game
	 */
	public boolean isInGame() {
		if(state == PlayerState.ESCAPED|| state == PlayerState.DEAD){
			return false;
		}
		return true;
	}

	/**
	 * For losing a fight
	 */
	public void lostCombat() {
		gameLogic.kill(this);
	}
	
	
}
