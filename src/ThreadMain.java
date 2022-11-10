
public class ThreadMain extends Thread {
	IDMConsoleService idm;
     
    public ThreadMain(int num){
    	idm = new IDMConsoleService();
    }
    
    public void run(){
        for(int l = 0;l<1;l++){
           try{
        	   idm.Run();
           }catch(Exception exc){
               
           }
           
        }
    } 
}
