package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class FriendLabel extends JLabel implements MouseListener{

	private FriendBar _friendBar;
	private String _friendName;
	private boolean _isVisible = true;
	
	public FriendLabel(FriendBar friendBar, String friendName){
		_friendBar = friendBar;
		_friendName = friendName;
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Toggle visibility of this calendar
		if(_isVisible){
			_isVisible = false;
			_friendBar.setCalVisible(_friendName, false);
			//set text to gray
			this.setForeground(new Color(112,112,112));
		}
		else{
			_isVisible = true;
			_friendBar.setCalVisible(_friendName, true);
			this.setForeground(Color.BLACK);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// If this is visible, show only this calendar
		if(_isVisible){
			_friendBar.hideAllVisible(_friendName);
			this.setForeground(Color.GREEN);
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// If calendar visible, show all calendars that were visible before
		if(_isVisible){
			_friendBar.showAllHidden();
			this.setForeground(Color.BLACK);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	
	
	
}