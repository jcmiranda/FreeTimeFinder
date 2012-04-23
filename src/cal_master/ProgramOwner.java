package cal_master;

import calendar.Owner;

public class ProgramOwner implements Owner {

	private String _name = null;
	
	public void setName(String name){
		_name = name;
	}
	
	@Override
	public String getName() {
		return _name;
	}

}
