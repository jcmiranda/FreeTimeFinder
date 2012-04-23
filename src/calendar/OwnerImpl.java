package calendar;

public class OwnerImpl implements Owner {
	private String _name;
	
	public OwnerImpl(String name) {
		_name = name;
	}
	
	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) {
		_name = name;
	}

}
