package Scheduler;

import Controller.EngineHibernate;
import Controller.EngineProgramInstalled;

public class EngineThreadHibernate extends Thread{
	EngineHibernate idm;
    
    public EngineThreadHibernate(String kode_cabang){
    	idm = new EngineHibernate();
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
