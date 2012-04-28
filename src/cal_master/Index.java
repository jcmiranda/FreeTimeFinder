package cal_master;

import java.util.Collection;
import java.util.HashMap;

public class Index {
	//public enum IndexType {When2MeetEvent, ProgramOwner, GCal, GCalImporter};
	private HashMap<String, StoredDataType> _items = new HashMap<String, StoredDataType>();
	
	public void addItem(String uniqueID, StoredDataType type) {
		_items.put(uniqueID, type);
	}
	
	public void removeItem(String id) {
		_items.remove(id);
	}
	
	public boolean hasItem(String id) {
		return _items.keySet().contains(id);
	}
	
	public Collection<String> getFiles() {
		return _items.keySet();
	}
	
	public StoredDataType getType(String id) {
		return _items.get(id);
	}
}
