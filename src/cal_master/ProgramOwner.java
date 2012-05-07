package cal_master;

import calendar.Owner;

/**
 * Represents all the information a user might want to store about themselves
 * For now, that information is limited to the name they use to respond to events
 *
 */

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
