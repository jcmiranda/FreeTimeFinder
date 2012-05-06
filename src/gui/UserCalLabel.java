package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import cal_master.Communicator;

/**
 * 
 * Represents the label that the user clicks on to choose whether or not this subcalendar of their calendar (specified by 
 * _calName) will be displayed in the calendar section of the gui. If it is selected, a check mark will appear. The user 
 * must still press "refresh" in order for their changes to take effect.
 * 
 *
 */

public class UserCalLabel extends JLabel implements MouseListener{

	private String _calName;
	private ImageIcon _checkIcon, _blankIcon; 
	private Communicator _communicator;
	private Color _background;
	boolean _selected = false;
	
	public UserCalLabel(String name, Communicator communicator){
		_calName = name;
		_communicator = communicator;
		_checkIcon = new ImageIcon("check_mark.png");
		_blankIcon = new ImageIcon("grey_square.png"); 
		_background = this.getBackground();
		
		this.setText(_calName);
		this.setHorizontalAlignment(LEFT);
		this.addMouseListener(this);
	}
	
	/**
	 * Toggle check icon on and off based on value passed in 
	 * @param b - show check if true, hide it otherwise
	 */
	public void setSelected(boolean b){
		_selected = b;
		if(_selected)
			this.setIcon(_checkIcon);
		else
			this.setIcon(_blankIcon);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		//toggle check mark
		setSelected(!_selected);
		//change stored rep of calendar for refresh
		_communicator.setSelectedInUserCal(_calName, _selected);
		this.setBackground(_background);
		this.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize()));
		this.setBackground(_background);
	}

	@Override
	public void mouseExited(MouseEvent e) { 
		this.setFont(new Font(this.getFont().getName(), Font.PLAIN, this.getFont().getSize()));
		this.setBackground(_background);
	}

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }
	
}
