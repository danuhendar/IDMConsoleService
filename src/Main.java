import Controller.Global_function;
import Scheduler.CheckThread;
import Scheduler.EngineThreadBootTime;
import Scheduler.EngineThreadHardwareInfo;
import Scheduler.EngineThreadHibernate;
import Scheduler.EngineThreadMemoryInfo;
import Scheduler.EngineThreadProgramInstalled;

public class Main {
	public static void main(String args[]) {
		try {
			
			
			Global_function gf = new Global_function(false);
			//System.err.println(branch_code); 
			String tanggal_jam = gf.get_tanggal_curdate_curtime();
			gf.WriteFile("timemessage.txt", "", tanggal_jam, false);
			//System.err.println("topic : "+branch_code);
			EngineThreadProgramInstalled t1 = new EngineThreadProgramInstalled("1");
			t1.start();
			
			EngineThreadHibernate t2 = new EngineThreadHibernate("1");
			t2.start();
			
			EngineThreadMemoryInfo t3 = new EngineThreadMemoryInfo("1");
			t3.start();
			
			EngineThreadHardwareInfo t4 = new EngineThreadHardwareInfo("1");
			t4.start();
			
			EngineThreadBootTime t5 = new EngineThreadBootTime("1");
			t5.start();
			
			
			
			CheckThread tn = new CheckThread();
			tn.start();
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
}
