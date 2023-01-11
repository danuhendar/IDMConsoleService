import java.io.FileInputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class IDMConsoleService {
	MqttClient client_transreport;
	String ip_mongo_db;
	Global_function gf = new Global_function(true);
	Connection con;
	int counter = 1;
	
	String Parser_TASK, Parser_ID, Parser_SOURCE, Parser_COMMAND, Parser_OTP, Parser_TANGGAL_JAM, Parser_VERSI,
			Parser_HASIL, Parser_FROM, Parser_TO, Parser_SN_HDD, Parser_IP_ADDRESS, Parser_STATION, Parser_CABANG,
			Parser_NAMA_FILE, Parser_CHAT_MESSAGE, Parser_REMOTE_PATH, Parser_LOCAL_PATH, Parser_SUB_ID;

	public void UnpackJSON(String json_message) {

		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(json_message);
		} catch (org.json.simple.parser.ParseException ex) {
			System.out.println("message json : " + json_message);
			System.out.println("message error : " + ex.getMessage());
			// ex.printStackTrace();
			// Logger.getLogger(IDMReport.class.getName()).log(Level.SEVERE, null, ex);
		}

		try {
			Parser_TASK = obj.get("TASK").toString();
		} catch (Exception ex) {
			Parser_TASK = "";
		}
		try {
			Parser_ID = obj.get("ID").toString();
		} catch (Exception exc) {
			Parser_ID = "";
		}
		try {
			Parser_SOURCE = obj.get("SOURCE").toString();
		} catch (Exception exc) {
			Parser_SOURCE = "";
		}
		try {
			Parser_COMMAND = obj.get("COMMAND").toString();
		} catch (Exception exc) {
			Parser_COMMAND = "";
		}
		try {
			Parser_OTP = obj.get("OTP").toString();
		} catch (Exception exc) {
			Parser_OTP = "";
		}

		try {
			Parser_TANGGAL_JAM = obj.get("TANGGAL_JAM").toString();
		} catch (Exception exc) {
			Parser_TANGGAL_JAM = "";
		}
		try {
			Parser_VERSI = obj.get("RESULT").toString().split("_")[7];
		} catch (Exception exc) {
			try {
				Parser_VERSI = obj.get("VERSI").toString();
			} catch (Exception exc1) {
				Parser_VERSI = "";
			}

		}

		try {
			Parser_HASIL = obj.get("HASIL").toString();
			Parser_FROM = obj.get("FROM").toString();
			Parser_TO = obj.get("TO").toString();

		} catch (Exception exc) {
			Parser_HASIL = "";
			Parser_FROM = "";
			Parser_TO = "";
		}

		try {
			Parser_SN_HDD = obj.get("SN_HDD").toString();
		} catch (Exception exc) {
			try {
				Parser_SN_HDD = obj.get("SN_HDD").toString();
			} catch (Exception exc1) {
				Parser_SN_HDD = "";
			}

		}
		try {
			Parser_IP_ADDRESS = obj.get("IP_ADDRESS").toString();
		} catch (Exception exc) {
			try {
				Parser_IP_ADDRESS = obj.get("IP_ADDRESS").toString();
			} catch (Exception exc1) {
				Parser_IP_ADDRESS = "";
			}

		}

		try {
			Parser_STATION = obj.get("STATION").toString();
		} catch (Exception exc) {
			try {
				Parser_STATION = obj.get("STATION").toString();
			} catch (Exception exc1) {
				Parser_STATION = "";
			}

		}

		try {
			Parser_CABANG = obj.get("CABANG").toString();
		} catch (Exception exc) {
			try {
				Parser_CABANG = obj.get("CABANG").toString();
			} catch (Exception exc1) {
				Parser_CABANG = "";
			}
		}

		try {
			Parser_NAMA_FILE = obj.get("NAMA_FILE").toString();
		} catch (Exception exc) {
			Parser_NAMA_FILE = "";
		}
		try {
			Parser_CHAT_MESSAGE = obj.get("CHAT_MESSAGE").toString();
		} catch (Exception exc) {
			Parser_CHAT_MESSAGE = "";
		}
		try {
			Parser_REMOTE_PATH = obj.get("REMOTE_PATH").toString();
		} catch (Exception exc) {
			Parser_REMOTE_PATH = "";
		}
		try {
			Parser_LOCAL_PATH = obj.get("LOCAL_PATH").toString();
		} catch (Exception exc) {
			Parser_LOCAL_PATH = "";
		}
		try {
			Parser_SUB_ID = obj.get("SUB_ID").toString();
		} catch (Exception exc) {
			Parser_SUB_ID = "";
		}

	}
	 
	public void subs_topic_return_from_listener(String topic_from_listener,int qos_message_command) {
		try {
			String rtopic_validasi_command = topic_from_listener;
			System.out.println("SUBS : "+rtopic_validasi_command);
			client_transreport.subscribe(rtopic_validasi_command, qos_message_command, new IMqttMessageListener() {
				@Override
				public void messageArrived(final String topic, final MqttMessage message) throws Exception {
						// ----------------------------- FILTER TOPIC NOT CONTAINS -------------------------------//
						Date HariSekarang_run = new Date();
						String payload = new String(message.getPayload());
						String msg_type = "";
						String message_ADT_Decompress = "";
						if(topic.contains("BYLINE")) {
							
						}else {
							try {
								message_ADT_Decompress = gf.ADTDecompress(message.getPayload());
								msg_type = "json";
							} catch (Exception exc) {
								message_ADT_Decompress = payload;
								msg_type = "non json";
							}
							System.err.println(message_ADT_Decompress);
							gf.WriteLog("MESSAGE RECEIVED FROM LISTENER : "+message_ADT_Decompress, true);
							counter++;
							UnpackJSON(message_ADT_Decompress);
							gf.PrintMessage2("RECV > "+rtopic_validasi_command, counter, msg_type, topic, Parser_TASK, Parser_FROM,
									Parser_TO, null, HariSekarang_run);
							
							
							String hasil_insert_db = gf.InsTransReport(Parser_TASK, Parser_ID, Parser_SOURCE, Parser_COMMAND, Parser_OTP,
									Parser_TANGGAL_JAM, Parser_VERSI, Parser_HASIL, Parser_TO, Parser_FROM, Parser_SN_HDD,
									Parser_IP_ADDRESS, Parser_STATION, Parser_CABANG, Parser_NAMA_FILE, Parser_CHAT_MESSAGE,
									Parser_REMOTE_PATH, Parser_LOCAL_PATH, Parser_SUB_ID, true, "INSERT", "transreport");
							
							
							String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
		                    //System.err.println("res_message_to_idmconsole : "+res_message);
		                    byte[] convert_message = res_message.getBytes("US-ASCII");
		                    byte[] bytemessage = convert_message;
		                    String topic_dest = "RES_MEMORY_INFO/"+Parser_TO+"/";
		                    //System.out.println("TOPIC SEND MONITORING CONSOLE : "+topic_dest);
		                    //gf.PublishMesssageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
		                    gf.PublishMessageNotDocumenter(topic_dest, bytemessage, counter, res_message,1,topic_from_listener);
	                         
		                    //Thread.sleep(5000);
		                   
						}
						
				}
			});
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	} 
	
	public void Memory_info_Service(String topic_setting,int qos_message_command) {
		try {
			
			String rtopic_validasi_command = topic_setting;
			System.out.println("SUBS : "+rtopic_validasi_command);
			client_transreport.subscribe(rtopic_validasi_command, qos_message_command, new IMqttMessageListener() {
				@Override
				public void messageArrived(final String topic, final MqttMessage message) throws Exception {
						// ----------------------------- FILTER TOPIC NOT CONTAINS -------------------------------//
						Date HariSekarang_run = new Date();
						String payload = new String(message.getPayload());
						String msg_type = "";
						if(topic.contains("BYLINE")) {
							
						}else {
							String message_ADT_Decompress = "";
							try {
								message_ADT_Decompress = gf.ADTDecompress(message.getPayload());
								msg_type = "json";
							} catch (Exception exc) {
								message_ADT_Decompress = payload;
								msg_type = "non json";
							}
							//System.out.println(message_ADT_Decompress);
							gf.WriteLog("MESSAGE RECEIVED : "+message_ADT_Decompress, true);
							counter++;
							UnpackJSON(message_ADT_Decompress);
							gf.PrintMessage2("RECV > "+rtopic_validasi_command, counter, msg_type, topic, Parser_TASK, Parser_FROM,
									Parser_TO, null, HariSekarang_run);
							
							
							String hasil_insert_db = gf.InsTransReport(Parser_TASK, Parser_ID, Parser_SOURCE, Parser_COMMAND, Parser_OTP,
									Parser_TANGGAL_JAM, Parser_VERSI, Parser_HASIL, Parser_TO, Parser_FROM, Parser_SN_HDD,
									Parser_IP_ADDRESS, Parser_STATION, Parser_CABANG, Parser_NAMA_FILE, Parser_CHAT_MESSAGE,
									Parser_REMOTE_PATH, Parser_LOCAL_PATH, Parser_SUB_ID, Boolean.parseBoolean(gf.en.getTampilkan_query_console()), "INSERT", "transreport");
							
							
							String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
		                    //System.err.println("res_message_otp : "+res_message);
		                    byte[] convert_message = res_message.getBytes("US-ASCII");
		                    byte[] bytemessage = gf.compress(convert_message);
		                    String topic_dest = ""+Parser_CHAT_MESSAGE+"/";
		                    //System.out.println("TOPIC DEST : "+topic_dest);
		                    gf.PublishMessageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
		                    
		                    String topic_send_return_listener = Parser_CHAT_MESSAGE+"/#";
		                    subs_topic_return_from_listener(topic_send_return_listener,qos_message_command);
						}
						  
				}
			});
		}catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void List_program_terinstall(String topic_setting,int qos_message_command) {
		try {
			String rtopic_validasi_command = topic_setting;
			System.out.println("SUBS : "+rtopic_validasi_command);
			client_transreport.subscribe(rtopic_validasi_command, qos_message_command, new IMqttMessageListener() {
				@Override
				public void messageArrived(final String topic, final MqttMessage message) throws Exception {
						// ----------------------------- FILTER TOPIC NOT CONTAINS -------------------------------//
						Date HariSekarang_run = new Date();
						String payload = new String(message.getPayload());
						String msg_type = "";
						if(topic.contains("BYLINE")) {
							
						}else {
							String message_ADT_Decompress = "";
							try {
								message_ADT_Decompress = gf.ADTDecompress(message.getPayload());
								msg_type = "json";
							} catch (Exception exc) {
								message_ADT_Decompress = payload;
								msg_type = "non json";
							}
							//System.out.println(message_ADT_Decompress);
							//gf.WriteLog("MESSAGE RECEIVED : "+message_ADT_Decompress, true);
							counter++;
							UnpackJSON(message_ADT_Decompress);
							gf.PrintMessage2("RECV > "+rtopic_validasi_command, counter, msg_type, topic, Parser_TASK, Parser_FROM,
									Parser_TO, null, HariSekarang_run);
							
							
							String hasil_insert_db = gf.InsTransReport(Parser_TASK, Parser_ID, Parser_SOURCE, Parser_COMMAND, Parser_OTP,
									Parser_TANGGAL_JAM, Parser_VERSI, Parser_HASIL, Parser_TO, Parser_FROM, Parser_SN_HDD,
									Parser_IP_ADDRESS, Parser_STATION, Parser_CABANG, Parser_NAMA_FILE, Parser_CHAT_MESSAGE,
									Parser_REMOTE_PATH, Parser_LOCAL_PATH, Parser_SUB_ID, Boolean.parseBoolean(gf.en.getTampilkan_query_console()), "INSERT", "transreport");
							
							
							String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
		                    //System.err.println("res_message_otp : "+res_message);
		                    byte[] convert_message = res_message.getBytes("US-ASCII");
		                    byte[] bytemessage = gf.compress(convert_message);
		                    String topic_dest = "COMMAND/"+Parser_CHAT_MESSAGE+"/";
		                    System.out.println("TOPIC DEST : "+topic_dest);
		                    gf.PublishMessageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
		                    
		                    //String topic_send_return_listener = Parser_CHAT_MESSAGE+"/ServiceProgramInstalled/";
		                    //subs_topic_return_from_listener(topic_send_return_listener,qos_message_command);
						}
						  
				}
			});
		}catch(Exception exc) {
			
		}
	}
	
	public void Boot_time(String topic_setting,int qos_message_command) {
		try {
			String rtopic_validasi_command = topic_setting;
			System.out.println("SUBS : "+rtopic_validasi_command);
			client_transreport.subscribe(rtopic_validasi_command, qos_message_command, new IMqttMessageListener() {
				@Override
				public void messageArrived(final String topic, final MqttMessage message) throws Exception {
						// ----------------------------- FILTER TOPIC NOT CONTAINS -------------------------------//
						Date HariSekarang_run = new Date();
						String payload = new String(message.getPayload());
						String msg_type = "";
						if(topic.contains("BYLINE")) {
							
						}else {
							String message_ADT_Decompress = "";
							try {
								message_ADT_Decompress = gf.ADTDecompress(message.getPayload());
								msg_type = "json";
							} catch (Exception exc) {
								message_ADT_Decompress = payload;
								msg_type = "non json";
							}
							//System.out.println(message_ADT_Decompress);
							//gf.WriteLog("MESSAGE RECEIVED : "+message_ADT_Decompress, true);
							counter++;
							UnpackJSON(message_ADT_Decompress);
							gf.PrintMessage2("RECV > "+rtopic_validasi_command, counter, msg_type, topic, Parser_TASK, Parser_FROM,
									Parser_TO, null, HariSekarang_run);
							
							
							String hasil_insert_db = gf.InsTransReport(Parser_TASK, Parser_ID, Parser_SOURCE, Parser_COMMAND, Parser_OTP,
									Parser_TANGGAL_JAM, Parser_VERSI, Parser_HASIL, Parser_TO, Parser_FROM, Parser_SN_HDD,
									Parser_IP_ADDRESS, Parser_STATION, Parser_CABANG, Parser_NAMA_FILE, Parser_CHAT_MESSAGE,
									Parser_REMOTE_PATH, Parser_LOCAL_PATH, Parser_SUB_ID, Boolean.parseBoolean(gf.en.getTampilkan_query_console()), "INSERT", "transreport");
							
							
							String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
		                    //System.err.println("res_message : "+res_message);
		                    byte[] convert_message = res_message.getBytes("US-ASCII");
		                    byte[] bytemessage = gf.compress(convert_message);
		                    String topic_dest = Parser_CHAT_MESSAGE;
		                    //System.out.println("TOPIC DEST : "+topic_dest);
		                    gf.PublishMessageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
		                    
		                    //String topic_send_return_listener = Parser_CHAT_MESSAGE+"/ServiceProgramInstalled/";
		                    //subs_topic_return_from_listener(topic_send_return_listener,qos_message_command);
						}
						  
				}
			});
		}catch(Exception exc) {
			
		}
	}
	
	
	public void Hibernate(String topic_setting,int qos_message_command) {
		try {
			String rtopic_validasi_command = topic_setting;
			System.out.println("SUBS : "+rtopic_validasi_command);
			client_transreport.subscribe(rtopic_validasi_command, qos_message_command, new IMqttMessageListener() {
				@Override
				public void messageArrived(final String topic, final MqttMessage message) throws Exception {
						// ----------------------------- FILTER TOPIC NOT CONTAINS -------------------------------//
						Date HariSekarang_run = new Date();
						String payload = new String(message.getPayload());
						String msg_type = "";
						if(topic.contains("BYLINE")) {
							
						}else {
							String message_ADT_Decompress = "";
							try {
								message_ADT_Decompress = gf.ADTDecompress(message.getPayload());
								msg_type = "json";
							} catch (Exception exc) {
								message_ADT_Decompress = payload;
								msg_type = "non json";
							}
							//System.out.println(message_ADT_Decompress);
							//gf.WriteLog("MESSAGE RECEIVED : "+message_ADT_Decompress, true);
							counter++;
							UnpackJSON(message_ADT_Decompress);
							gf.PrintMessage2("RECV > "+rtopic_validasi_command, counter, msg_type, topic, Parser_TASK, Parser_FROM,
									Parser_TO, null, HariSekarang_run);
							
							
							String hasil_insert_db = gf.InsTransReport(Parser_TASK, Parser_ID, Parser_SOURCE, Parser_COMMAND, Parser_OTP,
									Parser_TANGGAL_JAM, Parser_VERSI, Parser_HASIL, Parser_TO, Parser_FROM, Parser_SN_HDD,
									Parser_IP_ADDRESS, Parser_STATION, Parser_CABANG, Parser_NAMA_FILE, Parser_CHAT_MESSAGE,
									Parser_REMOTE_PATH, Parser_LOCAL_PATH, Parser_SUB_ID, Boolean.parseBoolean(gf.en.getTampilkan_query_console()), "INSERT", "transreport");
							
							
							String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
		                    //System.err.println("res_message : "+res_message);
		                    byte[] convert_message = res_message.getBytes("US-ASCII");
		                    byte[] bytemessage = gf.compress(convert_message);
		                    String topic_dest = Parser_CHAT_MESSAGE;
		                    System.out.println("TOPIC DEST : "+topic_dest);
		                    gf.PublishMessageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
		                    
		                    //String topic_send_return_listener = Parser_CHAT_MESSAGE+"/ServiceProgramInstalled/";
		                    //subs_topic_return_from_listener(topic_send_return_listener,qos_message_command);
						}
						  
				}
			});
		}catch(Exception exc) {
			
		}
	}
	
	public void Hardware_Info(String topic_setting,int qos_message_command) {
		try {
			String rtopic_validasi_command = topic_setting;
			System.out.println("SUBS : "+rtopic_validasi_command);
			client_transreport.subscribe(rtopic_validasi_command, qos_message_command, new IMqttMessageListener() {
				@Override
				public void messageArrived(final String topic, final MqttMessage message) throws Exception {
						// ----------------------------- FILTER TOPIC NOT CONTAINS -------------------------------//
						Date HariSekarang_run = new Date();
						String payload = new String(message.getPayload());
						String msg_type = "";
						if(topic.contains("BYLINE")) {
							
						}else {
							String message_ADT_Decompress = "";
							try {
								message_ADT_Decompress = gf.ADTDecompress(message.getPayload());
								msg_type = "json";
							} catch (Exception exc) {
								message_ADT_Decompress = payload;
								msg_type = "non json";
							}
							//System.out.println(message_ADT_Decompress);
							//gf.WriteLog("MESSAGE RECEIVED : "+message_ADT_Decompress, true);
							counter++;
							UnpackJSON(message_ADT_Decompress);
							gf.PrintMessage2("RECV > "+rtopic_validasi_command, counter, msg_type, topic, Parser_TASK, Parser_FROM,
									Parser_TO, null, HariSekarang_run);
							
							
							String hasil_insert_db = gf.InsTransReport(Parser_TASK, Parser_ID, Parser_SOURCE, Parser_COMMAND, Parser_OTP,
									Parser_TANGGAL_JAM, Parser_VERSI, Parser_HASIL, Parser_TO, Parser_FROM, Parser_SN_HDD,
									Parser_IP_ADDRESS, Parser_STATION, Parser_CABANG, Parser_NAMA_FILE, Parser_CHAT_MESSAGE,
									Parser_REMOTE_PATH, Parser_LOCAL_PATH, Parser_SUB_ID, Boolean.parseBoolean(gf.en.getTampilkan_query_console()), "INSERT", "transreport");
							
							
							//-- hardware info trigger -//
							String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
		                    System.err.println("res_message : "+res_message);
		                    byte[] convert_message = res_message.getBytes("US-ASCII");
		                    byte[] bytemessage = gf.compress(convert_message);
		                    String topic_dest = Parser_CHAT_MESSAGE;
		                    System.out.println("TOPIC DEST HARDWARE INFO : "+topic_dest);
		                    gf.PublishMessageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
		                    
		                    //-- physical trigger -//
		                    String Chat_message = "";
		                    if(Parser_CHAT_MESSAGE.contains("HARDWARE_INFO/G")) {
		                    	Chat_message = Parser_CHAT_MESSAGE.split("/")[1]+"/";
		                    }else if(Parser_CHAT_MESSAGE.substring(0,1).equals("G")){
		                    	Chat_message = Parser_CHAT_MESSAGE;
		                    }else {
		                    	Chat_message = Parser_CHAT_MESSAGE.replace("HARDWARE_INFO", "COMMAND");
		                    }
		                    
		                    Parser_TASK = "BC_POWERSHELL_COMMAND";
		                    Parser_COMMAND = "$disk = Get-PhysicalDisk | Select-Object -Property DeviceId,Model,MediaType,BusType | ConvertTo-Csv  -Delimiter ',' -NoTypeInformation | select -skip 1 ; $vol = Get-Volume | Select-Object -Property DriveLetter,FileSystemType,DriveType,HealthStatus,OperationalStatus,SizeRemaining,Size | ConvertTo-Csv -Delimiter ',' -NoTypeInformation | select -skip 1 ;$disk,$vol;";
		                    		//"$disk = Get-PhysicalDisk | Select-Object -Property DeviceId,Model,MediaType,BusType  |  ConvertTo-Csv -NoTypeInformation;$vol = Get-Volume | Select-Object -Property DriveLetter,FileSystemType,DriveType,HealthStatus,OperationalStatus,SizeRemaining,Size | ConvertTo-Csv -NoTypeInformation; Write-Host \"\" $disk,$vol;";
		                    		//"Get-PhysicalDisk  | Select-Object -Property DeviceId,Model,MediaType,BusType,HealthStatus | ConvertTo-Csv -NoTypeInformation";
		                    Parser_FROM = "ServicePhysicalDisk";
							String res_message_physical_disk = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Chat_message,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
		                    System.err.println("res_message_physical_disk : "+res_message_physical_disk);
		                    byte[] convert_message_physical_disk = res_message_physical_disk.getBytes("US-ASCII");
		                    byte[] bytemessage_physical_disk = gf.compress(convert_message_physical_disk);
		                    String topic_dest_physical_disk = Chat_message;
		                    System.out.println("TOPIC DEST PHYSICAL DISK : "+topic_dest_physical_disk);
		                    gf.PublishMessageAndDocumenter(topic_dest_physical_disk, bytemessage_physical_disk, counter, res_message_physical_disk,1);
		                    
		                    
		                    
		                    
						}
						  
				}
			});
		}catch(Exception exc) {
			
		}
	}

	public void Run() {
		System.out.println("=================================          START         ==================================");
		try {
			client_transreport = gf.get_ConnectionMQtt();
			int qos_message_command = 0;
			String cabang[] = gf.en.getCabang().split(",");
			String topic_setting[] = gf.en.getTopic().split(",");
			Memory_info_Service(topic_setting[0],qos_message_command);
			List_program_terinstall(topic_setting[1],qos_message_command);
			Boot_time(topic_setting[2],qos_message_command);
			Hibernate(topic_setting[3],qos_message_command);
			Hardware_Info(topic_setting[4],qos_message_command);
			
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
