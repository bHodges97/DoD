

import java.util.HashSet;
import java.util.Set;

public class Element {
	public Element(String name) {
		this.name = name;
	}
	public String name = "";
	public Set<Element> nodes = new HashSet<Element>();
	public String value = "";
	
	
	public void print(int depth){
		for(int i = 0;i < depth;i++){
			System.out.print("--");
		}
		System.out.print(name+"\n");
		if(nodes.isEmpty()){
			for(int i = 0;i < depth;i++){
				System.out.print("--");
			}
			System.out.print("--"+value+"\n");
			return;
		}else{
			for(Element e : nodes){
				e.print(depth+1);
			}
		}
	}
}
