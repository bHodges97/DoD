/**
 * x,y coordinates of an object
 *
 */
public class Position implements Messageable{
	int x = 0;
	int y = 0;
	
	/**
	 * Make a copy of the parameter position
	 * @param pos 
	 */
	public Position(Position pos){
		x = pos.x;
		y = pos.y;
	}
	
	/**
	 * Constructor
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Check if the parameters are numerically equivalent
	 * @param pos1 the first position
	 * @param pos2 the second position
	 * @return true if the parameters are numerically equivalent
	 */
	public static boolean equals(Position pos1, Position pos2){
		return (pos1.x == pos2.x && pos1.y == pos2.y);
	}
	
	/**
	 * Get direction to the given position
	 * @param pos
	 * @return N,W,S or E 
	 */
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
	
	/**
	 * Get adjacent tile in the given direction
	 * @param direction
	 * @return the adjacent tile
	 */
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
	
	/**
	 * Check if parameter is numerically equivalent to this
	 * @param pos the position to check
	 * @return true if numerically equivalent
	 */
	public boolean equalsto(Position pos){
		return x == pos.x && y == pos.y;
	}
	
	@Override
	public String toString(){
		return x+","+y;
	}

	@Override
	public String getInfo() {
		return "<POSITION><X>"+x+"</X><Y>"+y+"</Y></POSITION>";
	}

	public void add(Position b) {
		x+=b.x;
		y+=b.y;
	}
	public static Position subtract(Position a, Position b) {
		return new Position(a.x-b.x,a.y-b.y);
	}
	
	public static Position multiply(Position a, int scaler){
		return new Position(a.x * scaler,a.y * scaler);
	}

	public void multiply(float scaler) {
		x*=scaler;
		y*=scaler;
	}

	public double magnitude() {
		return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
	}

	/**
	 * Crude normalisation because this vector is an integer
	 * @return
	 */
	public Position normalise() {
		int normx,normy;
		if(x > 0){
			normx = 1;
		}else if(x < 0){
			normx = -1;
		}else{
			normx = 0;
		}
		if(y > 0){
			normy = 1;
		}else if(y < 0){
			normy = -1;
		}else{
			normy = 0;
		}
		return new Position(normx,normy);
	}
	
}
