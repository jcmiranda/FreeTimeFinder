package calendar;

// Owner for a when2meet event has a name and an ID
// Their name and their id come from parsing the HTML for the when2meet event
// Also note that the "owner" or a when2meet is not necessarily the creator of it.
public class When2MeetOwner implements Owner {
	private String _name;
	private int _id;

	public When2MeetOwner(String name, int id) {
		_name = name;
		_id = id;
	}

	@Override
	public String getName() {
		return _name;
	}
	
	// If the name of an owner is set within our program, they won't have
	// and id yet, so id is set to -1
	public void setName(String name) {
		_name = name; 
		_id = -1;
	};
	
	// Sets the id for a when2meet owner
	public void setID(int id){
		_id = id;
	}
	
	// Gets the id for a when2meet owner
	public int getID() { return _id; };
}
