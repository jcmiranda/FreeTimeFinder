package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import cal_master.Communicator;
import calendar.When2MeetEvent;

public class EventLabel extends JLabel implements MouseListener{

	private String _name, _id;
	private Communicator _communicator;
	CalendarGui _gui;
	
	public EventLabel(String name, String id, Communicator communicator, CalendarGui gui){
		super();
		_id = id;
		_communicator = communicator;
		_gui = gui;
		setName(name);
		this.addMouseListener(this);
	}
	
	public void setName(String name){
		_name = name;
		this.setText(name);
	}
	
	public String getID(){
		return _id;
	}
	
	public String getName(){
		return _name;
	}
	
	public void refresh(){
		When2MeetEvent event = _communicator.getW2MByID(_id);
//		if(event != null && event.hasUpdates()){
//			this.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize()));
//		}
//		else{
//			this.setFont(new Font(this.getFont().getName(), Font.PLAIN, this.getFont().getSize()));
//		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		When2MeetEvent toReturn = _communicator.getW2M(_id);
		if(toReturn != null){
			_gui.setSlots(toReturn);
		}
		
		//TODO : deal with null (which should never happen)
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//TODO: figure out underlining or change color
		this.setForeground(Color.GREEN);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO undo whatever we do in mouseEntered
		this.setForeground(Color.BLACK);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
