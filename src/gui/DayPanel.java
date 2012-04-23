package gui;

import static gui.GuiConstants.BG_COLOR;


import static gui.GuiConstants.GRAY_OUT_COLOR;
import static gui.GuiConstants.LINE_COLOR;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.CalendarSlots;
import calendar.Availability;
import calendar.Response;


public class DayPanel extends JPanel{

	DateTime _today;
	int _startHour = 0;
	int _numHours = 24;
	private ArrayList<CalendarSlots> _slots;
	private ArrayList<ArrayList<Response>> _responses;
	private Boolean _active = true;


	public DayPanel(){
		super();
		_slots = new ArrayList<CalendarSlots>();
		_responses = new ArrayList<ArrayList<Response>>();
		this.setBackground(BG_COLOR);
		this.repaint();
	}
	
	public DayPanel(int startHour, int numHours, DateTime today, boolean active){
		super();
		_startHour = startHour;
		_numHours = numHours;
		_today = today;
		_active = active;
		_slots = new ArrayList<CalendarSlots>();
		_responses = new ArrayList<ArrayList<Response>>();
		this.setBackground(BG_COLOR);
		this.repaint();
	}

	public int getStartHour() {
		return _startHour;
	}

	public void setStartHour(int startHour) {
		_startHour = startHour;
	}

	public int getNumHours() {
		return _numHours;
	}

	public void setNumHours(int numHours) {
		_numHours = numHours;
	}
	
	public void setActive(Boolean active){
		_active = active;
	}

	public boolean isActive(){
		return _active;
	}


	public ArrayList<CalendarSlots> getSlots() {
		return _slots;
	}

	public void setSlots(ArrayList<CalendarSlots> slots){
		_slots = slots;
	}

	public ArrayList<ArrayList<Response>> getResponses() {
		return _responses;
	}

	public void setResponses(ArrayList<ArrayList<Response>> responses){
		_responses = responses;
	}
	
	public void addSlotCal(CalendarSlots cal){
		_slots.add(cal);
	}
	
	public void addRespCal(ArrayList<Response> cal){
		_responses.add(cal);
	}
	
	public void setDay(DateTime today){
		_today = today;
	}
	
	public DateTime getDay(){
		return _today;
	}
	
	public void nextWeek(){
		_today = _today.plusDays(7);
	}

	public void lastWeek(){
		_today = _today.minusDays(7);
	}
	
	
	public void flipAvail(int slotNum){
		if (_slots.get(0).getAvail(slotNum) == Availability.busy) {
			_slots.get(0).setAvail(slotNum, Availability.free);
		} else {
			_slots.get(0).setAvail(slotNum, Availability.busy);
		}
	}

	private void drawLines(Graphics2D brush){

		brush.setColor(LINE_COLOR);
		for (int i=1; i<_numHours; i++){
			brush.drawLine(0, i*this.getHeight()/_numHours, this.getWidth(), i*this.getHeight()/_numHours);
		}
	}

	
	public void paintComponent(Graphics g){

		super.paintComponent(g);
		Graphics2D brush = (Graphics2D) g;
		drawLines(brush);

		int numCals = _responses.size();
		for (int i=0; i < numCals; i++){
			int numResps = _responses.get(i).size();
			for (int j = 0; j < numResps; j++){
				_responses.get(i).get(j).setGfxParams(i*this.getWidth()/numCals+j*this.getWidth()/numCals/numResps,
						this.getWidth()/numCals/numResps,
						this.getHeight(),
						_startHour,
						_startHour+_numHours);
				_responses.get(i).get(j).paint(brush);
			}
		}

		for (int i=0; i<_slots.size(); i++) {
			_slots.get(i).setGfxParams(this.getWidth(),	this.getHeight());
			_slots.get(i).paint(brush);
		}
		if (!_active){
			brush.setColor(GRAY_OUT_COLOR);
			brush.fillRect(0, 0, getWidth(), getHeight());
		}

	}



}
