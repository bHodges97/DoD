

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Element implements Messageable{
	public Element(String name) {
		this.tag = name;
	}
	public String tag = "";
	public Set<Element> children = new HashSet<Element>();
	public String value = "";
	
	
	public void print(int depth){
		for(int i = 0;i < depth;i++){
			System.out.print("--");
		}
		System.out.print(tag+"\n");
		if(children.isEmpty()){
			for(int i = 0;i < depth;i++){
				System.out.print("--");
			}
			System.out.print("--"+value+"\n");
			return;
		}else{
			for(Element e : children){
				e.print(depth+1);
			}
		}
	}

	public boolean isBoolean(){
		return value.equals("true") || value.equals("false");
	}
	
	public boolean toBoolean() {
		return value.equals("true");
	}
	
	public boolean isInt(){
		try{
			Integer.valueOf(value);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public int toInt(){
		return Integer.parseInt(value);
	}

	public void toLobbyPlayer(LobbyPlayer player){
		for(Element child:children){
			String tag = child.tag;
			String value = child.value;
			if(tag.equals("READY")){
				if(child.isBoolean()){
					player.ready = child.toBoolean();
				}
			}else if(tag.equals("NAME")){
				player.name = value;
			}else if(tag.equals("COLOR")){
				if(child.isInt()){
					player.color = new Color(child.toInt());
				}
			}else if(tag.equals("BOT")){
				if(child.isBoolean()){
					player.isBot = child.toBoolean();
				}
			}else if(tag.equals("CONNECTED")){
				if(child.isBoolean()){
					player.connected = child.toBoolean();
				}
			}else if(tag.equals("ID")){
				if(child.isInt()){
					player.id = child.toInt();
				}
			}
		}	
	}

	@Override
	public String getInfo() {
		String contents = value;
		for(Element child:children){
			contents+=child.getInfo();
		}
		return "<"+tag+">" +contents+ "</"+tag+">";
	}
	
	public Element getChild(String name){
		for(Element child:children){
			if(child.tag.equals(name)){
				return child;
			}
		}
		return null;
	}
	
	
}
