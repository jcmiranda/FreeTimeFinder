package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.joda.time.DateTime;

import calendar.Availability;

public class ClickableDayPanel extends DayPanel{

	public ClickableDayPanel(){
		super();
	}
	
	public ClickableDayPanel(int startHour, int numHours, DateTime today,
			boolean active) {
		super(startHour, numHours, today, active);
		calListener cl = new calListener();
		this.addMouseListener(cl);
		this.addMouseMotionListener(cl);
	}


	class calListener implements MouseListener, MouseMotionListener{

		Availability flipMode = Availability.busy;

		@Override
		public void mousePressed(MouseEvent arg0) {
			int slotNum = (int) ((double) arg0.getY()/getHeight()*_numHours*4);
			flipAvail(slotNum);
			flipMode = getSlots().get(0).getAvail(slotNum);
			repaint();
		}		

		@Override
		public void mouseDragged(MouseEvent arg0) {

			int slotNum = (int) ((double) arg0.getY()/getHeight()*_numHours*4);

			if (getSlots().get(0).getAvail(slotNum) != flipMode) {
				flipAvail(slotNum);
				repaint();
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
