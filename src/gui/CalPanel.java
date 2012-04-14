package gui;

import static gui.GuiConstants.*;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import org.joda.time.DateTime;

import calendar.CalendarGroup;
import calendar.CalendarResponses;
import calendar.Response;


public class CalPanel extends JPanel{

	CalendarGroup _calGroup;
	DateTime _startDateTime;
	int _numDays = 7;
	int _startHour = 0;
	int _endHour = 24;
	int _numHours;

	public CalPanel(CalendarGroup calGroup){
		super();
		_calGroup = calGroup;
		// ASK ABOUT THIS VVV
		_startDateTime = (new DateTime(2012,3,5,9,0));

		if (!calGroup.getCalendars().isEmpty()){
			_startHour = _calGroup.getCalendars().get(0).getStartTime().getHourOfDay();
			_endHour = _calGroup.getCalendars().get(0).getEndTime().getHourOfDay();

			for (CalendarResponses calImp: _calGroup.getCalendars()){
				_startHour = Math.min(_startHour, calImp.getStartTime().getHourOfDay());
				_endHour = Math.max(_endHour, calImp.getEndTime().getHourOfDay());
			}
		}

		_numHours = _endHour - _startHour;

				calListener cl = new calListener();
				this.addMouseListener(cl);
				this.addMouseMotionListener(cl);

	}

	public void paint(Graphics g){


		g.setColor(BLANK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(LINE_COLOR);

		for (int i=1; i<_numHours; i++){
			g.drawLine(0, i*this.getHeight()/_numHours, this.getWidth(), i*this.getHeight()/_numHours);
		}
		for (int i=1; i<_numDays; i++){
			g.drawLine(i*this.getWidth()/_numDays, 0, i*this.getWidth()/_numDays, this.getHeight());
		}

		g.setColor(BLOCK_COLOR);
		for (CalendarResponses calImp: _calGroup.getCalendars()){
			for (Response resp : calImp.getResponses()){
				double startX = ((int) ((double) (resp.getStartTime().getDayOfWeek()-1)/_numDays*this.getWidth())) ;
				int startY = ((int) ((double) ((double) resp.getStartTime().getMinuteOfHour()/60 + resp.getStartTime().getHourOfDay() - _startHour)/_numHours*this.getHeight()));
				int endY = ((int) ((double) ((double) resp.getEndTime().getMinuteOfHour()/60 + resp.getEndTime().getHourOfDay() - _startHour)/_numHours*this.getHeight()));
				g.fillRoundRect((int) startX, startY, this.getWidth()/_numDays, endY - startY, RECT_ARC_DIM, RECT_ARC_DIM);

			}
		}
	}

	class calListener implements MouseListener, MouseMotionListener{

		Response resp;

		@Override
		public void mousePressed(MouseEvent arg0) {

			int dayOfWeek = (int) (((double) arg0.getX()/getWidth()*_numDays)) +1;
			int startHour = (int) (((double) arg0.getY()/getHeight()*_numHours) + _startHour);
			resp = new Response(new DateTime(2012,3,dayOfWeek+_startDateTime.getDayOfMonth()-1,startHour,0),
					new DateTime(2012,3,dayOfWeek,startHour+1,0));
			_calGroup.getCalendars().get(0).addResponse(resp);
			repaint();
			//			paintBusy(arg0.getX(), arg0.getY());
		}		

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
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
	//
	//	//TALK ABOUT UTILITY OF INVERT
	//	void paintBusy(int mouseX, int mouseY){
	//
	//
	//		_calGroup.getCalendars().get(0).addResponse(new Response
	//				(new DateTime(2012,3,dayOfWeek,startHour,0),
	//						new DateTime(2012,3,dayOfWeek,startHour+1,0)));
	//	}


}
