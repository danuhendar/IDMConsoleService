package Controller;

import java.sql.Connection;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class EngineBootTime {
	MqttClient client_transreport;
	String ip_mongo_db;
	Global_function gf = new Global_function(true);
	Connection con;
	int counter = 1;
	
	public EngineBootTime() {
		
	}
	
	
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
							
							System.out.println("STATION :"+Parser_STATION);
							System.out.println("REMOTE_PATH :"+Parser_REMOTE_PATH);
							if(Parser_REMOTE_PATH == "") {
								String sp_station[] = Parser_STATION.replace("[", "").replace("]", "").replace("\"", "").split(",");
								for(int a = 0;a<sp_station.length;a++) {
									String in_station = sp_station[a];
									String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
				                    //System.err.println("res_message : "+res_message);
				                    byte[] convert_message = res_message.getBytes("US-ASCII");
				                    byte[] bytemessage = gf.compress(convert_message);
				                    String topic_dest = Parser_CHAT_MESSAGE.replace("STATION", in_station);
				                    //System.out.println("TOPIC DEST : "+topic_dest);
				                    gf.PublishMessageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
				                    Thread.sleep(15000);
				                    String tanggal_jam = gf.get_tanggal_curdate_curtime();
				        			gf.WriteFile("timemessage.txt", "", tanggal_jam, false);
								}
							}else {
								String sp_branch_code[] = Parser_REMOTE_PATH.split(",");
								for(int b = 0;b<sp_branch_code.length;b++) {
									String branch = sp_branch_code[b];
									String sp_station[] = Parser_STATION.replace("[", "").replace("]", "").replace("\"", "").split(",");
									for(int a = 0;a<sp_station.length;a++) {
										String in_station = sp_station[a];
										Parser_CHAT_MESSAGE = branch+"/"+in_station+"/";
										Parser_CABANG = branch;
										String res_message = gf.CreateMessage(Parser_TASK,Parser_ID,Parser_SOURCE,Parser_COMMAND,Parser_OTP,Parser_TANGGAL_JAM,Parser_VERSI,Parser_HASIL,Parser_FROM,Parser_TO,Parser_SN_HDD,Parser_IP_ADDRESS,Parser_STATION,Parser_CABANG,"",Parser_NAMA_FILE,Parser_CHAT_MESSAGE,Parser_REMOTE_PATH,Parser_LOCAL_PATH,Parser_SUB_ID);
					                    //System.err.println("res_message : "+res_message);
					                    byte[] convert_message = res_message.getBytes("US-ASCII");
					                    byte[] bytemessage = gf.compress(convert_message);
					                    String topic_dest = Parser_CHAT_MESSAGE;
					                    System.out.println("TOPIC DEST : "+topic_dest);
					                    gf.PublishMessageAndDocumenter(topic_dest, bytemessage, counter, res_message,1);
					                    Thread.sleep(7000);
					                    String tanggal_jam = gf.get_tanggal_curdate_curtime();
					        			gf.WriteFile("timemessage.txt", "", tanggal_jam, false);
									}
								}
								
							}
							
							
		                    
		                    //String topic_send_return_listener = Parser_CHAT_MESSAGE+"/ServiceProgramInstalled/";
		                    //subs_topic_return_from_listener(topic_send_return_listener,qos_message_command);
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
			Boot_time(topic_setting[2],qos_message_command);
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
