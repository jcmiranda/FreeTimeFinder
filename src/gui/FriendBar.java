package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;

import calendar.CalendarSlots;
import calendar.Event;
import calendar.Event.CalByThatNameNotFoundException;

public class FriendBar extends JPanel {
	
	private Event _event;
	private ArrayList<FriendLabel> _friendLabels = new ArrayList<FriendLabel>();
	private ArrayList<CalendarSlots> _tempInvisible = new ArrayList<CalendarSlots>();
	private JPanel _scrollPaneInner = new JPanel();
	private JScrollPane _scrollPane;
	private GridLayout _spiLayout = new GridLayout(0, 1, 0, 10); // new GroupLayout(_scrollPaneInner);
	private GroupLayout _layout = new GroupLayout(this);
	private CalendarGui _gui;
	
	public FriendBar(CalendarGui gui){
		_gui = gui;
		_scrollPaneInner.setLayout(_spiLayout);
		_scrollPane = new JScrollPane(_scrollPaneInner);
		_scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		_scrollPane.setBorder(null);
		
		this.setLayout(_layout);
		_layout.setAutoCreateGaps(true);
		_layout.setAutoCreateContainerGaps(true);
	
		
		SequentialGroup vertSeqGrp = _layout.createSequentialGroup();
		ParallelGroup horizParGrp = _layout.createParallelGroup(GroupLayout.Alignment.CENTER);
	
		horizParGrp.addComponent(_scrollPane);
		vertSeqGrp.addComponent(_scrollPane);

		_layout.setHorizontalGroup(horizParGrp);
		_layout.setVerticalGroup(vertSeqGrp);
		
		
		this.add(_scrollPane);
	}
	
	public void mySetSize(Dimension d) {
		int width = d.width;
		int height = d.height;
		this.setSize(d);
		_scrollPane.setSize(width, height);
	}
	
	public void setLabelColor(String labelName, Color color){
		for(FriendLabel label : _friendLabels){
			if(label.getText() == labelName){
				label.setColor(color);
				break;
			}
		}
	}
	
	
	private void initLabels(){
		_friendLabels.clear();
		
		_scrollPaneInner.removeAll();
		_scrollPaneInner.setLayout(_spiLayout);
		//_scrollPaneInner.setPreferredSize(new Dimension((int) (GuiConstants.FRAME_WIDTH*0.75), 25));

		if(_event != null)
			for(CalendarSlots cal : _event.getCalendars()){

				FriendLabel toAdd = new FriendLabel(this, cal.getOwner().getName(), cal.getColor());
				toAdd.setVisible(true);

				_friendLabels.add(toAdd);

				_scrollPaneInner.add(toAdd); 
			}
	
		this.revalidate();
		_gui.repaint();
	}
	
	public void setEvent(Event event){
		_event = event;
		initLabels();
	}
	
	public void setCalVisible(String name, boolean visible){
		CalendarSlots cal;
		try {
			if(_event != null){
				cal = _event.getCalByName(name);
				cal.setVisible(visible);
				_gui.updateBestTimes();
				_gui.repaint();
			}
		} catch (CalByThatNameNotFoundException e) {
			//TODO handle this
		}
		
	}
	
	public void hideAllVisible(String calToKeep){
		if(_event != null)
			for(CalendarSlots cal : _event.getCalendars()){
				if(cal.isVisible() && !cal.getOwner().getName().equals(calToKeep)){
					_tempInvisible.add(cal);
					cal.setVisible(false);
				}
			}
		_gui.repaint();
	}
	
	public void showAllHidden(){
		for(CalendarSlots cal : _tempInvisible){
			cal.setVisible(true);
		}
		_tempInvisible.clear();
		_gui.repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		for(FriendLabel label: _friendLabels){
			label.repaint();
		}
	}
	
	
	
	
	
	
	

}
