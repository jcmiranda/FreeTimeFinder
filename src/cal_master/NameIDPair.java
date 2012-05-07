package cal_master;

/**
 * Represents name, id tuple for use by GUI (e.g. to display all event names with out actually having all of the 
 * events in the GUI)
 *
 */

public class NameIDPair {
	private String _name;
	private String _id;
	
	public NameIDPair(String name, String i) {
		_name = name;
		_id = i;
	}
	
	public String getName() {return _name;}
	public String getID() {return _id;}
}
