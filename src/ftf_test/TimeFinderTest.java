package ftf_test;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;

import calendar.*;



public class TimeFinderTest {

	
	public static void main(String[] args){
		
		
	}
	
	
	
	
	
	private class TestCalender implements Calendar{

		private DateTime _start, _end;
		private Collection<Response> _resp;
		private Owner _own;
		
		public TestCalender(DateTime start, DateTime end, Owner o, Collection<Response> r){
			_start = start;
			_end = end;
			_own = o;
			_resp = r;
		}
		@Override
		public DateTime getStartTime() {
			return _start;
		}

		@Override
		public DateTime getEndTime() {
			return _end;
		}

		@Override
		public Owner getOwner() {
			// TODO Auto-generated method stub
			return _own;
		}

		@Override
		public Collection<Response> getResponses() {
			return (Collection<Response>) _resp;
		}
		
	}
	
	private class TestCalGroup implements CalendarGroup {

		private Collection<Calendar> _cals;
		
		@Override
		public Collection<Calendar> getCalendars() {
			// TODO Auto-generated method stub
			return _cals;
		}

		@Override
		public void addCalendar(Calendar c) {
			_cals.add(c);
			
		}
	}
	
	private class TestOwner implements Owner {

		private String _name;
		
		public TestOwner(String name){
			_name = name;
		}
		
		@Override
		public String getName() {
			return _name;
		}
		
	}
}
