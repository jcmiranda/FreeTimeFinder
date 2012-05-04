package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.joda.time.DateTime;

import cal_master.Communicator;
import cal_master.Communicator.URLAlreadyExistsException;
import calendar.Event;

public class EventPanel extends JPanel {

	private ArrayList<EventLabel> _eventLabels = new ArrayList<EventLabel>();
	private ArrayList<RemoveEventLabel> _removeLabels = new ArrayList<RemoveEventLabel>();
	private Communicator _communicator;
	private JButton _addButton;
	private JButton _createButton;
	private CalendarGui _gui;
	private JLabel _titleLabel;
	private JScrollPane _eventsScrollPane;
	private JPanel _scrollPaneInner = new JPanel();
	private GroupLayout _layout= new GroupLayout(this), _spiLayout = new GroupLayout(_scrollPaneInner);
	
	public EventPanel(Communicator communicator, CalendarGui gui){
		_communicator = communicator;
		_gui = gui;
		_addButton = new JButton("Add Event");
		_addButton.addActionListener(new AddEventListener());
		_createButton = new JButton ("Create Event");
		_createButton.addActionListener(new CreateEventListener());
		
		_titleLabel = new JLabel("My Events");
		Font newLabelFont=new Font(_titleLabel.getFont().getName(),Font.BOLD,
				_titleLabel.getFont().getSize());  

		_titleLabel.setFont(newLabelFont);
		
		GridLayout sPILayout = new GridLayout(10, 2);
		sPILayout.setVgap(0);
		
		_scrollPaneInner.setLayout(sPILayout);
		_eventsScrollPane = new JScrollPane(_scrollPaneInner);
		
		this.setUp();
	}
	
	private void setUp() {
		_scrollPaneInner.removeAll();
		_scrollPaneInner.setLayout(_spiLayout);
		_spiLayout.setAutoCreateGaps(true);
		_spiLayout.setAutoCreateContainerGaps(true);
		
		SequentialGroup spiVSGrp = _spiLayout.createSequentialGroup();
		ParallelGroup spiHPGrp =  _layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		int i=0;
		for(EventLabel label : _eventLabels){
			RemoveEventLabel rLabel = _removeLabels.get(i);
			spiVSGrp.addGroup(_spiLayout.createParallelGroup().addComponent(rLabel).addComponent(label));
			spiHPGrp.addGroup(_spiLayout.createSequentialGroup().addComponent(rLabel).addComponent(label));
			i++;
		}
		
		_spiLayout.setVerticalGroup(spiVSGrp);
		_spiLayout.setHorizontalGroup(spiHPGrp);
		
		this.setLayout(_layout);
		_layout.setAutoCreateGaps(true);
		_layout.setAutoCreateContainerGaps(true);
	
		
		SequentialGroup vertSeqGrp = _layout.createSequentialGroup();
		ParallelGroup horizParGrp = _layout.createParallelGroup(GroupLayout.Alignment.CENTER);
		
		vertSeqGrp.addComponent(_titleLabel);
		horizParGrp.addComponent(_titleLabel);
	
		horizParGrp.addComponent(_eventsScrollPane);
		vertSeqGrp.addComponent(_eventsScrollPane);
		vertSeqGrp.addGroup(_layout.createParallelGroup().addComponent(_addButton).addComponent(_createButton));

		horizParGrp.addGroup(_layout.createSequentialGroup().addComponent(_addButton).addComponent(_createButton));

		_layout.setHorizontalGroup(horizParGrp);
		_layout.setVerticalGroup(vertSeqGrp);
		
		this.revalidate();
		_gui.repaint();
	}
	
	
	public void addEvent(EventLabel label){
		_eventLabels.add(label);
		_removeLabels.add(new RemoveEventLabel(label.getID()));
		setUp();
	}
	
	public void addEvent(Event newEvent){
		EventLabel newLabel = new EventLabel(newEvent.getName(), String.valueOf(newEvent.getID()), _communicator, _gui);
		_eventLabels.add(newLabel);
		_removeLabels.add(new RemoveEventLabel(newLabel.getID()));
		setUp();
	}
	
	public void createEvent(String name, ArrayList<DateTime> selectedDates, int startHour, int endHour){
		addEvent(_communicator.createEvent(name, selectedDates, startHour, endHour));
	}
	
	public void addEvents(ArrayList<EventLabel> events){
		_eventLabels.addAll(events);
		for(EventLabel label : events){
			_removeLabels.add(new RemoveEventLabel(label.getID()));
		}
		setUp();
		_gui.repaint();
	}
	
	public void removeEvent(String idToRemove){
		for(int i=0; i<_eventLabels.size(); i++){
			if(_eventLabels.get(i).getID() == idToRemove){
				_eventLabels.remove(i);
				_removeLabels.remove(i);
				if(_gui.getEvent() != null)
					System.out.println("Event to remove: " + idToRemove + "\tGui Event: " + String.valueOf(_gui.getEvent().getID()));
				else
					System.out.println("GUI Event NULL");
				if(_gui.getEvent() != null && String.valueOf(_gui.getEvent().getID()).equals(idToRemove)){
					System.out.println("Event Panel Setting Event to Null");
					_gui.setEvent(null);
					_gui.repaint();
				}
				break;
			}
		}
		setUp();
	}
	
	public void refresh() {
		for(EventLabel label : _eventLabels){
			label.refresh();
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		for(EventLabel label: _eventLabels){
			label.repaint();
		}
	}
	
	private class CreateEventListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			AddEventDialog aEDialog = new AddEventDialog(EventPanel.this);
		}
		
	}
	
	private class AddEventListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
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
				if(newEvent.getName() == null){
					newEvent.setName("BLOOP");
				}
				addEvent(newEvent);
			}
			
			
		}
		
	}
	
	private class RemoveEventLabel extends JLabel implements MouseListener{

		
		private String _eventID;
		
		public RemoveEventLabel(String id){
			super("X");
			_eventID = id;
			this.addMouseListener(this);
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			int selection = JOptionPane.showConfirmDialog(null,"Are you sure you want to remove this event?", "", 
					JOptionPane.YES_NO_OPTION);
			if(selection == JOptionPane.YES_OPTION){
				_communicator.removeWhen2Meet(_eventID);
				removeEvent(_eventID);
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			this.setForeground(Color.RED);
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			this.setForeground(Color.BLACK);
		}

		@Override
		public void mousePressed(MouseEvent arg0) {}

		@Override
		public void mouseReleased(MouseEvent arg0) {}
		
		
	}
}
