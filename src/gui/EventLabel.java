package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import cal_master.Communicator;
import calendar.Event;

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
		this.setFont(new Font(GuiConstants.FONT_NAME, this.getFont().getStyle(), this.getFont().getSize()));
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
		Event event = _communicator.getEventByID(_id);
		if(event != null && event.hasUpdates()){
			this.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize()));
		}
		else{
			this.setFont(new Font(this.getFont().getName(), Font.PLAIN, this.getFont().getSize()));
		}
	}
	
	public void setEvent(){
		Event toReturn = _communicator.getW2M(_id);
		if(toReturn != null){
			_gui.setEvent(toReturn);
			toReturn.updatesViewed();
			_gui.repaint();
		}
		
		//TODO : deal with null (which should never happen)
	}
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		setEvent();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//TODO: figure out underlining?
		this.setForeground(new Color(238,99,99));
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//undo whatever we do in mouseEntered
		this.setForeground(Color.BLACK);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
}
