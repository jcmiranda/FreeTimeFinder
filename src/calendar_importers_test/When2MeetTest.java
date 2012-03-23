package calendar_importers_test;
import java.io.IOException;

import calendar_importers.When2MeetImporter;

public class When2MeetTest {
	public static void main(String[] args) throws IOException {
		String url = "http://www.when2meet.com/?353066-BlwWl";
		When2MeetImporter wtmi = new When2MeetImporter(url);
		wtmi.importFresh();
	}
}
