package cal_master;

import java.util.Collection;
import java.util.HashMap;

// Class used for storing the what information we currently have stored locally
// In order to be able to tell what has changed about a when2meet, we need to have data for
// how it used ot be to compare again. Our index is our way of keeping track of that data.

// When an item is "saved" locally, it is written to the filesystem as an xml file
// using xstream, and then added to the index so that on program startup the program knows how to
// recreate from its index

public class Index {
	// List of items in the index with ids to their types
	// Types are necessary for recreating objects b/c they need to be cast
	private HashMap<String, StoredDataType> _items = new HashMap<String, StoredDataType>();
	
	// Adds an item to the index (not this does not save the index, or save the item),
	// both of those need to be done by the caller
	public void addItem(String uniqueID, StoredDataType type) {
		_items.put(uniqueID, type);
	}
	
	// Removes an item from the index, again see comment for addItem
	public void removeItem(String id) {
		_items.remove(id);
	}
	
	// Returns true if an object with a given ID is contained within this index already
	public boolean hasItem(String id) {
		return _items.keySet().contains(id);
	}
	
	// Gets all of the files associated with this index
	public Collection<String> getFiles() {
		return _items.keySet();
	}
	
	// Gets the type of a given item in the index
	public StoredDataType getType(String id) {
		return _items.get(id);
	}
}
