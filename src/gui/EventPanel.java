package gui;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(_addButton);
		this.setLayout(new GridLayout(0,1));
		this.add(buttonPanel);
	}
	
	public void addEvent(EventLabel label){
		System.out.println("ADDING LABEL");
		_eventLabels.add(label);
		JPanel labelPanel = new JPanel();
		labelPanel.add(label);
		this.add(labelPanel);
		System.out.println(_eventLabels.size());
	}
	
	public void addEvents(ArrayList<EventLabel> events){
		_eventLabels.addAll(events);
		for(EventLabel label : events){
			JPanel labelPanel = new JPanel();
			labelPanel.add(label);
			this.add(labelPanel);
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
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		for(EventLabel label: _eventLabels){
			System.out.println(label.getName());
			label.repaint();
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
					if(url == null)
						break;
					newEvent = _communicator.addWhen2Meet(url);
					noEvent = false;
				} catch (MalformedURLException e) {
					url = JOptionPane.showInputDialog("Invalid URL. Try Again.");
				} catch (URLAlreadyExistsException e) {
					url = JOptionPane.showInputDialog("That event is already stored. Try Again.");
				} catch (IOException e) {
					url = JOptionPane.showInputDialog("Invalid URL. Try Again.");
				}
			}
			
			if(newEvent != null){
				System.out.println("ADDED EVENT");
				System.out.println(newEvent.getName());
				if(newEvent.getName() == null){
					newEvent.setName("BLOOP");
				}
				EventLabel newLabel = new EventLabel(newEvent.getName(), String.valueOf(newEvent.getID()), _communicator, _gui);
				addEvent(newLabel);
				//repaint();
				_gui.repaint();
				System.out.println("ADDED LABEL");
			}
			
			
		}
		
	}
}
