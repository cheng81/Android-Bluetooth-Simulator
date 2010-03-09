package dk.itu.btemu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {
	static final State _instance = new State();
	public static State getInstance(){ return _instance; }
	
	Map<String,Object> _state = new HashMap<String,Object>();
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T)_state.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> asList(Class<T> cls) {
		List<T> out = new ArrayList<T>();
		for(String k : _state.keySet()) {
			if(_state.get(k).getClass().equals(cls)) {
				T t = (T)_state.get(k);
				out.add(t);
			}
		}
		return out;
	}
	
	public void put(String key, Object value) {
		_state.put(key, value);
	}
	public boolean remove(String key) {
		if(_state.containsKey(key)) {
			_state.remove(key);
			return true;
		}
		return false;
	}
}
