package Scheduler;

import Controller.EngineBootTime;

public class EngineThreadBootTime extends Thread {
	EngineBootTime idm;
    
    public EngineThreadBootTime(String kode_cabang){
    	idm = new EngineBootTime();
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
