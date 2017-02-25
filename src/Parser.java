import java.util.Stack;

public class Parser {
	/**
	 * Delete me
	 * @param args
	 */
	public static void main(String[] args){
		new Parser().parse("<player><name>hello</name><id>2</id></player>").print(0);;
	}
	
	
	/**
	 * Basic parser for XML like structure used in my DOD for communication
	 * 
	 * @param in
	 * @return
	 */
	public Element parse(String in){
		//validate
		if(in.length() <= 2 || in.charAt(0) != '<' || in.charAt(1) == '/'){
			System.out.println("Could not be parsed:"+in);
			return null;
		}
		
		char[] chars = in.toCharArray();
		Stack<Element> stack = new Stack<Element>();
    	String nameBuilder = "";
    	String valueBuilder = "";
    	boolean buildName = false;
    	boolean buildNameEnd = false;
    	
    	for(int i = 0;i < chars.length;++i){
    		if(chars[i]== '<' && chars[i+1] != '/'){
    			//start of a tag, start building name
    			buildName = true;
    			valueBuilder = "";
    			nameBuilder = "";
    		}else if(chars[i]== '<' && chars[i+1] == '/'){
    			//end of a tag stop building value and start building name
    			++i;
    			buildName = true;
    			buildNameEnd = true;
    			stack.peek().value = valueBuilder;
    			valueBuilder = "";
    		}else if(chars[i] == '>'){
    			if(buildNameEnd){ 
    				//if this is not root element move it from stack to its parent
    				Element current = stack.peek();
    				if(stack.size()>1){
    					stack.pop();
    					stack.peek().nodes.add(current);
    				}
    				buildNameEnd = false;    				
    				
    				//Validation
    				if(!current.name.equals(nameBuilder)){
    					System.out.println("Could not be parsed(Invalid closing tag!):"+in);
    					return null;
    				}
    			}else{
    				//Add new element
    				stack.add(new Element(nameBuilder));	
    			}
    			nameBuilder = "";
    			buildName = false;
    		}else if(buildName){
    			//build name
    			nameBuilder+=chars[i];
    		}else{
    			//build value
    			valueBuilder+=chars[i];
    		}
    	}
    	//make sure there is only one element
    	if(stack.size()!=1){
			System.out.println("Could not be parsed(Tag is not closed!):"+in);
    		return null;
    	}
    	//return root element
    	return stack.pop();
	}


}
