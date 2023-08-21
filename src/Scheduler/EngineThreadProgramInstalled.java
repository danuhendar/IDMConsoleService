package Scheduler;

import Controller.EngineProgramInstalled;
import Controller.IDMConsoleService;

public class EngineThreadProgramInstalled extends Thread {
	EngineProgramInstalled idm;
     
    public EngineThreadProgramInstalled(String kode_cabang){
    	idm = new EngineProgramInstalled();
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
