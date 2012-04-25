package gui;

import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import calendar.EventUpdate;
import calendar.When2MeetEvent;

public class UpdatesPanel extends JScrollPane {
	
	private JTextArea _textArea = new JTextArea();
	private When2MeetEvent _event = null;
	
	public UpdatesPanel(){
		super();
		this.setViewportView(_textArea);
		_textArea.setEditable(false);
		
		//TODO : set size (invisible if no event selected?)
		
	}
	
	private void setText(){
		_textArea.setText("");
		ArrayList<EventUpdate> updates = _event.getUpdates();
		String newText = "";
		for(EventUpdate update : updates){
			newText += update.getMessage() + '\n';
		}
		_textArea.setText(newText);
	}
	
	public void setEvent(When2MeetEvent event){
		_event = event;
		setText();
	}
}
