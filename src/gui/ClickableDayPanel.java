package gui;

import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.joda.time.DateTime;
import org.joda.time.Days;

import calendar.Availability;
import calendar.CalendarSlots;

public class ClickableDayPanel extends DayPanel{

	public ClickableDayPanel(){
		super();
		calListener cl = new calListener();
		this.addMouseListener(cl);
		this.addMouseMotionListener(cl);
	}

	public ClickableDayPanel(int startHour, int numHours, DateTime today,
			boolean active) {
		super(startHour, numHours, today, active);
		calListener cl = new calListener();
		this.addMouseListener(cl);
		this.addMouseMotionListener(cl);
	}

	public CalendarSlots exportClicks(){
		return getSlots().getCalendars().get(0);
	}

	private int getDaysBetween(DateTime start, DateTime end){
		if(end.getYear() == start.getYear())
			return end.getDayOfYear() - start.getDayOfYear();
		else if(end.getYear() == start.getYear() + 1)
			return end.getDayOfYear() + 366 - start.getDayOfYear();
		return -1;
	}


	public void flipAvail(int slotNum){
		int day = getDaysBetween(getSlots().getStartTime(), getDay());

		CalendarSlots cal = getSlots().getCalendars().get(0);
		if(day >=0 && day < cal.numDays()){
			Availability avail = cal.getAvail(day, slotNum);
			if (avail == Availability.busy) {
				cal.setAvail(day, slotNum, Availability.free);
			} else {
				cal.setAvail(day, slotNum, Availability.busy);
			}
		}
	}


	class calListener implements MouseListener, MouseMotionListener{

		Availability flipMode;
		int originalSlot;

		@Override
		public void mousePressed(MouseEvent arg0) {
			if (isActive()){
				if (!(arg0.getY()<0 || arg0.getY()>ClickableDayPanel.this.getHeight() ||
						arg0.getX()<0 || arg0.getX()>ClickableDayPanel.this.getWidth())){
					originalSlot = (int) ((double) arg0.getY()/getHeight()*_numHours*4);
					flipAvail(originalSlot);
					System.out.println("today: " + getDay().getDayOfMonth());
					System.out.println("start: " + getSlots().getStartTime().getDayOfMonth());
					System.out.println("days between: " + getDaysBetween(getSlots().getStartTime(), getDay()));
					flipMode = getSlots().getCalendars().get(0).getAvail(getDaysBetween(getSlots().getStartTime(), getDay()), originalSlot);
					repaint();
				}
			}
		}		

		@Override
		public void mouseDragged(MouseEvent arg0) {
			if (isActive()){
				int slotNum = (int) ((double) arg0.getY()/getHeight()*_numHours*4);
				slotNum = Math.max(0, slotNum);

				for (int i=Math.min(originalSlot,slotNum); i<=Math.min(Math.max(originalSlot,slotNum), getSlots().getCalendars().get(0).getSlotsInDay()-1); i++){
					if (getSlots().getCalendars().get(0).getAvail(getDaysBetween(getSlots().getStartTime(), getDay()), i) != flipMode) {
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

}
