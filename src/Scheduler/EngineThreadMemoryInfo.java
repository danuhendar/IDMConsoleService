package Scheduler;

import Controller.EngineHibernate;
import Controller.EngineMemoryInfo;

public class EngineThreadMemoryInfo extends Thread {
EngineMemoryInfo idm;
    
    public EngineThreadMemoryInfo(String kode_cabang){
    	idm = new EngineMemoryInfo();
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
