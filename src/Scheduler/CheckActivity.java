package Scheduler;
import Controller.Global_function;
import Entity.Entity;

public class CheckActivity {
	
	Global_function gf = new Global_function(false);
	int batas_menit = Integer.parseInt(Entity.getBatasMenit());
	String kode_cabang = Entity.getCabang();
	
	public CheckActivity() {
			
	}
	
	public void Restart_Service(String command,String nama_service) {
		try {
			Runtime r = Runtime.getRuntime();
			Process proc = r.exec(command);
			System.out.println("command : "+command);
			gf.WriteLog("Restart "+nama_service+" : SUKSES", true);
		}catch(Exception exc) {
			
		}
	}
	
	
	public void Run() {
		try {
			//System.err.println("batas_menit : "+batas_menit);
			String get_last_time_message_incoming = gf.ReadFile("timemessage.txt").split(" ")[1];
			String waktu_kini = gf.get_tanggal_curdate_curtime().split(" ")[1];
			String selisih = gf.get_time_diff(waktu_kini, get_last_time_message_incoming);
			System.err.println("last_message : "+get_last_time_message_incoming+" VS waktu_kini : "+waktu_kini+" Sel. : "+selisih);
			String selisih_menit = selisih.split(":")[1];
			if(Integer.parseInt(selisih_menit) > batas_menit) {
				String command = "systemctl restart "+Entity.getId_reporter();
				//RestartIDMReporter_by_service("LISTENER_BACKEND_523/",command,"BC_SQL");
				Restart_Service(command, Entity.getId_reporter());
				Thread.sleep(10000);
				//System.exit(0);
			}
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	
	
		
}
