package calendar_importers_test;
import java.io.IOException;

import javax.swing.JOptionPane;

import calendar_importers.When2MeetImporter;

public class When2MeetTest {
	public static void main(String[] args) throws IOException {
		String str = JOptionPane.showInputDialog(null, "Enter When2Meet URL: ", 
				"http://www.when2meet.com/?353066-BlwWl", 1);
		//String url = "http://www.when2meet.com/?353066-BlwWl";
		When2MeetImporter wtmi = new When2MeetImporter(str);
		wtmi.importFresh();
	}
}
