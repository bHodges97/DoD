import java.util.HashMap;

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
				System.out.println("moving random "+direction);
				return "MOVE "+direction;
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
		if(targetPosition != null){
			if(pather.pathFind(currentPosition, targetPosition)){
				Position nextStep = pather.findNextStep();
				System.out.println("moving to player " + nextStep);
				playerPositions = new HashMap<Integer,Position>();
				return "MOVE "+currentPosition.getDirectionTo(nextStep);
			}else{
				System.out.println("Pather failed heading at direction of player");
				return "MOVE "+currentPosition.getDirectionTo(targetPosition);
			}
		}
		System.out.println("Couldn't decide");
		return "LOOK";
	}

	@Override
	public void sendOutput(String output) {
		this.output = output;
	}

	@Override
	public void processInfo(String message) {
		Element info = Parser.parse(message);
		if(info.tag.equals("TILES")){
			int i = 0;
			for(Element child:info.children){
				++i;
				Tile tile = child.toTile();
				map.addTile(tile.getDisplayChar(),tile.pos.x,tile.pos.y);
			}
		}else if(info.tag.equals("PLAYER")){
			Player player = info.toPlayer();
			if(player.id == id){
				currentPosition = player.position;
			}else if(player.getPlayerType() == Player.PlayerType.HUMANPLAYER && player.isInGame()){
				if(playerPositions.containsKey(player.id)){
					playerPositions.remove(player.id);
					playerPositions.put(player.id, player.position);
				}else{
					playerPositions.put(player.id, player.position);
				}
			}
		}
		
	}
}
