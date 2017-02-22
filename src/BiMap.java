import java.util.HashMap;

public class BiMap<Key, Value> {

    private HashMap<Key, Value> keyToValue;
    private HashMap<Value, Key> valueToKey;
    
    public void add(Key key, Value value) {
        if (!keyToValue.containsKey(key) && !valueToKey.containsKey(value)) {
            keyToValue.put(key, value);
            valueToKey.put(value, key);
        }
    }

}