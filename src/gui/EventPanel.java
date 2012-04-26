package gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import cal_master.Communicator;
import cal_master.Communicator.URLAlreadyExistsException;
import calendar.Event;
import calendar.When2MeetEvent;

public class EventPanel extends JPanel {

	private ArrayList<EventLabel> _eventLabels = new ArrayList<EventLabel>();
	private Communicator _communicator;
	private JButton _addButton;
	private CalendarGui _gui;
	private GroupLayout _layout= new GroupLayout(this);
	
	public EventPanel(Communicator communicator, CalendarGui gui){
		_communicator = communicator;
		_gui = gui;
		_addButton = new JButton("Add Event");
		_addButton.addActionListener(new AddEventListener());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(_addButton);
		//this.setLayout(new GridLayout(0,1));
		//this.add(buttonPanel);
		this.setLayout(_layout);
		_layout.setAutoCreateGaps(true);
		this.setUp();
	}
	
	private void setUp() {
		SequentialGroup vertSeqGrp = _layout.createSequentialGroup();
		ParallelGroup horizParGrp = _layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		vertSeqGrp.addComponent(_addButton);
		vertSeqGrp.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                GroupLayout.DEFAULT_SIZE, 15);
		horizParGrp.addComponent(_addButton);
		
		for(EventLabel label : _eventLabels){
			JPanel labelPanel = new JPanel();
			labelPanel.add(label);
			vertSeqGrp.addComponent(label);
			horizParGrp.addComponent(label);
		}
		
		_layout.setHorizontalGroup(horizParGrp);
		_layout.setVerticalGroup(vertSeqGrp);
	}
	
	public void addEvent(EventLabel label){
		_eventLabels.add(label);
		setUp();
//		JPanel labelPanel = new JPanel();
//		labelPanel.add(label);
//		this.add(labelPanel);
//		System.out.println(_eventLabels.size());
	}
	
	public void addEvents(ArrayList<EventLabel> events){
		_eventLabels.addAll(events);
//		for(EventLabel label : events){
//			JPanel labelPanel = new JPanel();
//			labelPanel.add(label);
//			this.add(labelPanel);
//		}
		setUp();
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
			//System.out.println(label.getName());
			label.repaint();
		}
	}
	
	private class AddEventListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//TODO: cancel option
			String url = JOptionPane.showInputDialog("Please enter the URL of the When2Meet you would like to add");
			Event newEvent = null;
			boolean noEvent = true;
			while(noEvent){
				try {
					if(url == null)
						break;
					newEvent = _communicator.addEvent(url);
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
