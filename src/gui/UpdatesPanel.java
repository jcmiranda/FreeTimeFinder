package gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.ParallelGroup;

import calendar.Event;
import calendar.EventUpdate;

public class UpdatesPanel extends JPanel {
	
	private JScrollPane _scrollPane = new JScrollPane();
	private JTextArea _textArea = new JTextArea();
	private JLabel _titleLabel = new JLabel();
	private Event _event = null;
	
	public UpdatesPanel(){
		super();
		_scrollPane.setViewportView(_textArea);
		_textArea.setEditable(false);
		_textArea.setLineWrap(true);
		Font newLabelFont=new Font(_titleLabel.getFont().getName(),Font.BOLD,
				_titleLabel.getFont().getSize());  

		_titleLabel.setFont(newLabelFont);
		_titleLabel.setText("Updates");
		_textArea.setText(" none");
		
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
		
		//TODO : set size (invisible if no event selected?)
		
	}
	
	private void setText(){
		_titleLabel.setText(_event.getName() + " Updates");
		_textArea.setText("");
		ArrayList<EventUpdate> updates = _event.getUpdates();
		String newText = ""; // _event.getName() + " Updates";
		for(EventUpdate update : updates){
			newText += " " + update.getMessage() + '\n';
		}
		_textArea.setText(newText);
	}
	
	public void setEvent(Event event){
		_event = event;
		setText();
	}
}
