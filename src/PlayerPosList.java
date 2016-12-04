import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PlayerPosList implements Iterable<Player>{
	List<Player> players = new ArrayList<Player>();
	List<int[]> positions = new ArrayList<int[]>();
	
	public PlayerPosList(){
		
	}
	
	public int[] update(Player player,int[] pos){
		int index = players.indexOf(player);
		int[] previousPos = positions.remove(index);
		positions.add(index,pos);
		return previousPos;
	}
	public void put(Player player,int[] pos){
		players.add(player);
		positions.add(pos);
	}
	public int[] get(Player player){
		int index = players.indexOf(player);
		return positions.get(index);
	}
	public Player getPlayer(int[] pos){
		int index = positions.indexOf(pos);
		return players.get(index);
	}
	public int[] remove(Player player){
		int index = players.indexOf(player);
		int[] pos = positions.remove(index);
		players.remove(player);
		return pos;
	}
	public Set<Player> keySet(){
		return new HashSet<Player>(players);
	}
	
	public int size(){
		return players.size();
	}
	
	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}
}
