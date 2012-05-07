package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.joda.time.DateTime;

import calendar.Availability;
import calendar.CalendarResponses;
import calendar.CalendarSlots;
import calendar.UserCal;

public class ClickableDayPanel extends Day{

	private CalendarSlots _clicks;
	private UserCal _userCal;

	public ClickableDayPanel(){
		super();
		CalListener cl = new CalListener();
		this.addMouseListener(cl);
		this.addMouseMotionListener(cl);
	}

	public ClickableDayPanel(int startHour, int numHours, DateTime today, int day, 
			boolean active) {
		super(startHour, numHours, today, day, active);
		CalListener cl = new CalListener();
		this.addMouseListener(cl);
		this.addMouseMotionListener(cl);
	}


	public CalendarSlots getClicks() {
		return _clicks;
	}

	public void setClicks(CalendarSlots clicks){

		_clicks = clicks;
	}

	public UserCal getUserCal() {
		return _userCal;
	}

	public void setUserCal(UserCal responses){
		_userCal = responses;
	}

	private int getDaysBetween(DateTime start, DateTime end){
		if(end.getYear() == start.getYear())
			return end.getDayOfYear() - start.getDayOfYear();
		else if(end.getYear() == start.getYear() + 1)
			return end.getDayOfYear() + 366 - start.getDayOfYear();
		return -1;
	}


	public void flipAvail(int slotNum){
		if(getClicks() != null){
			int day = getDaysBetween(getClicks().getStartTime(), getDay());

			CalendarSlots cal = getClicks();
			if(day >=0 && day < cal.numDays()){
				Availability avail = cal.getAvail(day, slotNum);
				if (avail == Availability.busy) {
					cal.setAvail(day, slotNum, Availability.free);
				} else {
					cal.setAvail(day, slotNum, Availability.busy);
				}
			}
		}
	}


	class CalListener implements MouseListener, MouseMotionListener{

		Availability flipMode;
		int originalSlot;

		@Override
		public void mousePressed(MouseEvent arg0) {
			if (isActive() && getClicks() != null){
				if (!(arg0.getY()<0 || arg0.getY()>ClickableDayPanel.this.getHeight() ||
						arg0.getX()<0 || arg0.getX()>ClickableDayPanel.this.getWidth())){
					originalSlot = (int) ((double) arg0.getY()/getHeight()*getNumHours()*4);
					flipAvail(originalSlot);
					System.out.println("today: " + getDay().getDayOfMonth());
					System.out.println("start: " + getClicks().getStartTime().getDayOfMonth());
					System.out.println("days between: " + getDaysBetween(getClicks().getStartTime(), getDay()));
					flipMode = getClicks().getAvail(getDaysBetween(getClicks().getStartTime(), getDay()), originalSlot);
					repaint();
				}
			}
		}		

		@Override
		public void mouseDragged(MouseEvent arg0) {
			if (isActive() && getClicks() != null){
				int slotNum = (int) ((double) arg0.getY()/getHeight()*getNumHours()*4);
				slotNum = Math.max(0, slotNum);

				for (int i=Math.min(originalSlot,slotNum); i<=Math.min(Math.max(originalSlot,slotNum), getClicks().getSlotsInDay()-1); i++){
					if (getClicks().getAvail(getDaysBetween(getClicks().getStartTime(), getDay()), i) != flipMode) {
						flipAvail(i);
						repaint();
					}	
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {	
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {		

		}

		@Override
		public void mouseMoved(MouseEvent arg0) {			
		}
	}



	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D brush = (Graphics2D) g;
		if (isActive()){
			if (_userCal!=null){
				int numCals = _userCal.getCalendars().size();
				for (CalendarResponses r: _userCal.getCalendars()){
					r.paint(brush, this, numCals, GuiConstants.RESPONSE_COLOR);
				}
				if(_clicks != null) {
					_clicks.paint(brush, this);
				}
			}
		}

	}
}
