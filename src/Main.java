
public class Main {
	public static void main(String args[]) {
		try {
			
			
			Global_function gf = new Global_function(false);
			//System.err.println(branch_code); 
			String tanggal_jam = gf.get_tanggal_curdate_curtime();
			gf.WriteFile("timemessage.txt", "", tanggal_jam, false);
			//System.err.println("topic : "+branch_code);
			ThreadMain t1 = new ThreadMain("");
			t1.start();
			CheckThread t2 = new CheckThread();
			t2.start();
			
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
}
