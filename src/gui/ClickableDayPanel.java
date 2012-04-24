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


	public void flipAvail(int slotNum){
		if (getSlots().getCalendars().get(0).getAvail(Days.daysBetween(getSlots().getStartTime(), getDay()).getDays(),
				slotNum) == Availability.busy) {
			getSlots().getCalendars().get(0).setAvail(Days.daysBetween(getSlots().getStartTime(), getDay()).getDays(), slotNum, Availability.free);
		} else {
			getSlots().getCalendars().get(0).setAvail(Days.daysBetween(getSlots().getStartTime(), getDay()).getDays(), slotNum, Availability.busy);
		}
	}


	class calListener implements MouseListener, MouseMotionListener{

		Availability flipMode;
		int originalSlot;

		@Override
		public void mousePressed(MouseEvent arg0) {
			//			if (!(arg0.getY()<0 || arg0.getY()>ClickableDayPanel.this.getHeight() ||
			//					arg0.getX()<0 || arg0.getX()>ClickableDayPanel.this.getWidth())){
			originalSlot = (int) ((double) arg0.getY()/getHeight()*_numHours*4);
			flipAvail(originalSlot);
			flipMode = getSlots().getCalendars().get(0).getAvail(Days.daysBetween(getSlots().getStartTime(), getDay()).getDays(), originalSlot);
			repaint();
			//		}
		}		

		@Override
		public void mouseDragged(MouseEvent arg0) {

			int slotNum = (int) ((double) arg0.getY()/getHeight()*_numHours*4);

			for (int i=Math.min(originalSlot,slotNum); i<=Math.max(originalSlot,slotNum); i++){
				if (getSlots().getCalendars().get(0).getAvail(Days.daysBetween(getSlots().getStartTime(), getDay()).getDays(), i) != flipMode) {
					flipAvail(i);
					repaint();
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
