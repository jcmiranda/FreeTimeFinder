package cal_master_test;

import java.net.MalformedURLException;

import cal_master.*;
import cal_master.Communicator.URLAlreadyExistsException;

public class xml_test {
	public static void main(String[] args) {
		Communicator com = new Communicator();
		com.startUp();
//		try {
//			com.addWhen2Meet("http://www.when2meet.com/?421831-IVgET");
//		} catch (URLAlreadyExistsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
		//com.saveAll();
	}
}
