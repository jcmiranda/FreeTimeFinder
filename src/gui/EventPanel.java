package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cal_master.Communicator;
import cal_master.Communicator.URLAlreadyExistsException;
import calendar.When2MeetEvent;

public class EventPanel extends JPanel {

	private ArrayList<EventLabel> _eventLabels = new ArrayList<EventLabel>();
	private Communicator _communicator;
	private JButton _addButton;
	private CalendarGui _gui;
	
	public EventPanel(Communicator communicator, CalendarGui gui){
		_communicator = communicator;
		_gui = gui;
		_addButton = new JButton("Add Event");
		_addButton.addActionListener(new AddEventListener());
	}
	
	public void addEvent(EventLabel label){
		_eventLabels.add(label);
		this.add(label);
	}
	
	public void addEvents(ArrayList<EventLabel> events){
		_eventLabels.addAll(events);
		for(EventLabel label : events){
			this.add(label);
		}
	}
	
	public void removeEvent(){
		//TODO
	}
	
	public void refresh() {
		for(EventLabel label : _eventLabels){
			label.refresh();
		}
	}
	
	private class AddEventListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//TODO: cancel option
			String url = JOptionPane.showInputDialog("Please enter the URL of the When2Meet you would like to add");
			When2MeetEvent newEvent = null;
			boolean noEvent = true;
			while(noEvent){
				try {
					newEvent = _communicator.addWhen2Meet(url);
					noEvent = false;
				} catch (MalformedURLException e) {
					url = JOptionPane.showInputDialog("Invalid URL. Try Again.");
				} catch (URLAlreadyExistsException e) {
					url = JOptionPane.showInputDialog("That event is already stored. Try Again.");
				}
			}
			
			if(newEvent != null){
				EventLabel newLabel = new EventLabel(newEvent.getName(), String.valueOf(newEvent.getID()), _communicator, _gui);
				addEvent(newLabel);
			}
			
			
		}
		
	}
}
