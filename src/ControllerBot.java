import java.util.HashMap;

/**
 * used to select bot actions
 * 
 */
public class ControllerBot extends Controller{
	private Map map = new Map();
	private String output = "START";
	private java.util.Map<Integer,Position> playerPositions = new HashMap<Integer,Position>();
	private Position currentPosition = new Position(0,0);
	private PathFinder pather;

	public ControllerBot(DODServer dodServer, int id,Player player) {
		super(dodServer,id,player);
		pather = new PathFinder(map);
	}

	@Override
	public String getInput() {
		try {
			//Even though each player has 0.4 second delay before acting
			//Bot needs a reaction delay to not make it too hard
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//try random direction
		if(playerPositions.isEmpty()){
			Tile next = pather.randomNextStep(currentPosition);
			if(next != null){
				char direction = currentPosition.getDirectionTo(next.pos);
				sendOutput("moving random "+direction+currentPosition);
				return "MOVE "+direction ;
			}
		}
		//pick closest player
		int closest = 0;
		Position targetPosition = null;
		for(Position pos:playerPositions.values()){
			int holder = PathFinder.estimateDistance(pos, currentPosition);
			if(holder > closest){
				targetPosition = pos;
				closest = holder;
			}
		}
		//now try path finding
		if(targetPosition != null){
			try{
				if(pather.pathFind(currentPosition, targetPosition)){
					sendOutput("Pather found path");
					Position nextStep = pather.findNextStep();
					playerPositions = new HashMap<Integer,Position>();
					return "MOVE "+currentPosition.getDirectionTo(nextStep);
				}else{
					sendOutput("Pather failed heading at direction of player");
					return "MOVE "+currentPosition.getDirectionTo(targetPosition);
				}
			}catch(NullPointerException e){
				e.printStackTrace();
				System.out.println("Debug0"+currentPosition);
				System.out.println("Debug1"+targetPosition);
			}
		}
		sendOutput("Couldn't decide on what to do" + currentPosition);
		return "LOOK";
	}

	@Override
	public void sendInfo(String message) {
		super.sendInfo(message);
		Element messageElement = Parser.parse(message);
		for(Element info:messageElement.children){
			if(info.tag.equals("TILES")){
				//process map info
				for(Element child:info.children){
					Tile tile = child.toTile();
					map.addTile(tile.getDisplayChar(),tile.pos.x,tile.pos.y);
				}
			}else if(info.tag.equals("PLAYER")){
				//process player info
				Player player = info.toPlayer();
				if(player.id == id){
					currentPosition = player.position;
				}else if(player.getPlayerType() == Player.PlayerType.HUMANPLAYER && player.isInGame()){
					playerPositions.put(player.id, player.position);
				}
			}
		}
	}
}
