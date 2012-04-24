package cal_master;

import java.util.Collection;
import java.util.HashMap;

import calendar.CalGroupType;

public class Index {
	public enum IndexType {When2MeetEvent, ProgramOwner, GCal};
	private HashMap<String, IndexType> _calGroups = new HashMap<String, IndexType>();
	
	public void addItem(String uniqueID, IndexType type) {
		_calGroups.put(uniqueID, type);
	}
	
	public Collection<String> getFiles() {
		return _calGroups.keySet();
	}
	
	public IndexType getType(String id) {
		return _calGroups.get(id);
	}
}
