package Scheduler;

import Controller.EngineHardwareInfo;

public class EngineThreadHardwareInfo extends Thread{
	
	EngineHardwareInfo idm;
    
    public EngineThreadHardwareInfo(String kode_cabang){
    	idm = new EngineHardwareInfo();
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
