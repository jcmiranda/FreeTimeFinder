package cal_master;

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
