package calendar;

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
	
	public void setName(String name) {
		_name = name; 
		_id = -1;
	};
	
	public void setID(int id){
		_id = id;
	}
	
	public int getID() { return _id; };
}
