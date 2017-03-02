

public class Position implements Messageable{
	int x = 0;
	int y = 0;
	
	public Position(){
		
	}
	
	public Position(Position pos){
		x = pos.x;
		y = pos.y;
	}
	
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public static boolean equals(Position pos1, Position pos2){
		return (pos1.x == pos2.x && pos1.y == pos2.y);
	}
	
	public char getDirectionTo(Position pos){
		if(pos.x > x){
			return 'E';
		}else if(pos.x < x){
			return 'W';
		}else if(pos.y > y){
			return 'S';
		}else if(pos.y < y){
			return 'N';
		}else{
			throw new IllegalArgumentException("Position overlaps current position");
		}
	}
	
	public Position getAdjacentTile(char direction){
		switch(direction){
		case 'N':
			return new Position(x,y-1);
		case 'W':
			return new Position(x-1,y);
		case 'E':
			return new Position(x+1,y);
		case 'S':
			return new Position(x,y+1);
		default:
			throw new IllegalArgumentException("Unexpected direction" + direction);
		}
	}
	
	public boolean equalsto(Position pos){
		return x == pos.x && y == pos.y;
	}
	
	public String toString(){
		return x+","+y;
	}

	@Override
	public String getInfo() {
		return "<POSITION><X>"+x+"</X><Y>"+y+"</Y></POSITION>";
	}
	
}
