package calendar_exporters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

import calendar.Availability;
import calendar.CalendarSlots;
import calendar.When2MeetEvent;

public class When2MeetExporter {
	private When2MeetEvent _event;
	private CalendarSlots _cal;
	
	public When2MeetExporter(When2MeetEvent event, CalendarSlots cal) {
		_event = event;
		_cal = cal;
	}
	
	private String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			// TODO fix
			System.err.println("Error encoding string" + s);
			System.exit(1);
			return "AHHHHH";
		}
	}
	
	private String keyValue(String key, String value) {
		return encode(key) + "=" + encode(value);
	}
	
	private String binaryAvailability() {
		String ret = "";
		for(int i = 0; i < _cal.getTotalSlots(); i++) {
			Availability avail = _cal.getAvail(i);
			if(avail == Availability.free)
				ret += "1";
			else
				ret += "0";
		}
		return ret;
	}
	
	private String slotIDs(ArrayList<Integer> slotIndices) {
		String ret = "";
		for(int i = 0; i < slotIndices.size(); i++) {
			ret += _event.getSlotID(slotIndices.get(i));
			if(i != slotIndices.size() - 1)
				ret += ",";
		}
		return ret;
	}
	
	public void updateAvailability(ArrayList<Integer> slotIndices, Availability avail) {
		URL url;
		String changeToAvailable = "false";
		if(avail == Availability.free) {
			changeToAvailable = "true";
		}
		
		for(int i = 0; i < slotIndices.size(); i++) {
			_cal.setAvail(slotIndices.get(i), avail);
		}
		
		try {
			url = new URL("http://www.when2meet.com/SaveTimes.php");
			URLConnection conn = url.openConnection();
			System.out.println(conn.getRequestProperties());
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			System.out.println("Person ID: " + _cal.getOwner().getID());
			String data = keyValue("person", ""+_cal.getOwner().getID());
			data += "&" + keyValue("event", ""+_event.getID());
			data += "&" + keyValue("slots", slotIDs(slotIndices));
			data += "&" + keyValue("availability", binaryAvailability());
			data += "&" + keyValue("ChangeToAvailable", changeToAvailable);
			
			//data = encode(data);
			System.out.println(data);
			//data = encode(data);
			
			
			//String data = "person=1498056&event=353066&slot=1330178400&availability=111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111&ChangeToAvailable=true";
			wr.write(data);
		    wr.flush();
		    
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		        System.out.println(line);
		    }
		    
		    wr.close();
			
					
		} catch (Exception e) {

		}
	}
	
	public void signIn(String username, String password){
		
	}
	


}
