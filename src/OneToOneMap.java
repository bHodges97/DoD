import java.util.HashMap;
import java.util.Set;


public class OneToOneMap<Key,Value> {
	java.util.Map<Key,Value> keyToValue = new HashMap<Key,Value>();
	java.util.Map<Value,Key> valueToKey = new HashMap<Value,Key>();
	
	
	public Value put(Key key,Value value){
		valueToKey.put(value, key);
		return keyToValue.put(key,value);
	}
	public Value get(Key key){
		return keyToValue.get(key);
	}
	public Key getKey(Value value){
		return valueToKey.get(value);
	}
	public Value remove(Key key){
		Value value = keyToValue.remove(key);
		valueToKey.remove(value);
		return value;
	}
	public Set<Key> keySet(){
		return keyToValue.keySet();
	}
}
