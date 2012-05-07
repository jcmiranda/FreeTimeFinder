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

/**
 * Class representing a day with user's calendars as well as overlayed W2M response input
 * @author roie
 *
 */
public class ClickableDayPanel extends Day{

	// Field representing a user's availability input
	private CalendarSlots _clicks;
	// Field representing a user's personal calendars
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

	/**
	 * For the current day, flip the user's availability at the slot represented by slotNum
	 * I.e. If free, set him busy, if busy set him free
	 * @param slotNum
	 */
	public void flipAvail(int slotNum){
		if(getClicks() != null){
			int day = CalendarSlots.getDaysBetween(getClicks().getStartTime(), getDay());

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

	/**
	 * Lisener to the clickable panel
	 * @author roie
	 *
	 */
	class CalListener implements MouseListener, MouseMotionListener{

		Availability flipMode;
		int originalSlot;

		/**
		 * On mouse pressed flip the availability of the pressed slot and record both the slot number and its state
		 */
		@Override
		public void mousePressed(MouseEvent arg0) {
			if (isActive() && getClicks() != null){
				if (!(arg0.getY()<0 || arg0.getY()>ClickableDayPanel.this.getHeight() ||
						arg0.getX()<0 || arg0.getX()>ClickableDayPanel.this.getWidth())){
					originalSlot = (int) ((double) arg0.getY()/getHeight()*getNumHours()*4);
					flipAvail(originalSlot);
					flipMode = getClicks().getAvail(CalendarSlots.getDaysBetween(getClicks().getStartTime(), getDay()), originalSlot);
					repaint();
				}
			}
		}		

		/**
		 * On mouse drag change all the slots between the current slot and the first slot clicked to the availability flipped to on the first click
		 */
		@Override
		public void mouseDragged(MouseEvent arg0) {
			if (isActive() && getClicks() != null){
				int slotNum = (int) ((double) arg0.getY()/getHeight()*getNumHours()*4);
				slotNum = Math.max(0, slotNum);

				for (int i=Math.min(originalSlot,slotNum); i<=Math.min(Math.max(originalSlot,slotNum), getClicks().getSlotsInDay()-1); i++){
					if (getClicks().getAvail(CalendarSlots.getDaysBetween(getClicks().getStartTime(), getDay()), i) != flipMode) {
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


	/**
	 * In addition to painting lines and BG color, also paint user's calendars for the day, and user's clickable input
	 */
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
