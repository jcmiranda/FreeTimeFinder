package gui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import calendar.Event;
import calendar.EventUpdate;

public class UpdatesPanel extends JPanel {
	
	private JScrollPane _scrollPane = new JScrollPane();
	private JTextArea _textArea = new JTextArea();
	private JLabel _titleLabel = new JLabel();
	private Event _event = null;
	private Font _newLabelFont, _textAreaFont;
	
	public UpdatesPanel(){
		super();
		_scrollPane.setViewportView(_textArea);
		_textArea.setEditable(false);
		_textArea.setLineWrap(true);
		_newLabelFont=new Font(_titleLabel.getFont().getName(),Font.BOLD,
				_titleLabel.getFont().getSize());  

		_titleLabel.setFont(_newLabelFont);
		_titleLabel.setText("Updates");
		_textArea.setText(" none");
		_textArea.setBackground(_titleLabel.getBackground());
		
		_textAreaFont = new Font(GuiConstants.FONT_NAME, _textArea.getFont().getStyle(), _textArea.getFont().getSize());
		_textArea.setFont(_textAreaFont);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(_titleLabel)
				.addComponent(_scrollPane));
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(_titleLabel)
				.addComponent(_scrollPane));
		
		this.add(_titleLabel);
		this.add(_scrollPane);
		
	}
	
	private void setText(){
		String eventName = "";
		if(_event != null)
			eventName = _event.getName();
		_titleLabel.setText(eventName + " Updates");
		_titleLabel.setFont(_newLabelFont);
		
		String newText = "";
		
		if(_event != null){
			ArrayList<EventUpdate> updates = _event.getUpdates();
			for(EventUpdate update : updates){
				newText += " " + update.getMessage() + '\n';
			}
		}
		_textArea.setText(newText);
		_textArea.setFont(_textAreaFont);
	}
	
	public void setEvent(Event event){
		_event = event;
		setText();
	}
}
