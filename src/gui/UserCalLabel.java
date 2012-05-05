package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import cal_master.Communicator;

public class UserCalLabel extends JLabel implements MouseListener{

	private String _calName;
	private ImageIcon _checkIcon; 
	private Communicator _communicator;
	boolean _selected = false;
	
	public UserCalLabel(String name, Communicator communicator){
		_calName = name;
		_communicator = communicator;
		_checkIcon= new ImageIcon("check_mark.png");
		this.setText(_calName);
		//this.setIcon(_checkIcon);
		this.setHorizontalAlignment(LEFT);
		this.addMouseListener(this);
	}
	
	public void setSelected(boolean b){
		_selected = b;
		if(_selected)
			this.setIcon(_checkIcon);
		else
			this.setIcon(null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		setSelected(!_selected);
		_communicator.setSelectedInUserCal(_calName, _selected);
		this.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }
	
}
