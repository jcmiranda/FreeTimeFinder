package calendar;

/**
 * Generic owner (where the purpose of the owner is to store meta-data about a particular calendar or set of calendars)
 *
 */

public interface Owner {
	String getName();
	void setName(String name);
}
