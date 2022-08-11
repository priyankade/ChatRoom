/**
* Project name: Chat Room Facility Application 
* Group Number: 18
* Teammates: 
  1. Priyanka De
  2. Sai Sruthi Chalasani
  3. Vardhan Barishetti  
* @date: 15-05-2022
* @pledge: I seriously and sincerely pledge by honor that I have abided by the Stevens honor code system
*/
//package ChatRoomFinalServer;
import java.net.*;
import java.io.*;

public class ServerThread extends Thread {

	private Server server;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String username;
	Object message;
	
	public ServerThread(Server server, Socket socket) throws IOException, ClassNotFoundException {
		// TODO Auto-generated constructor stub
		this.server = server;
		this.socket = socket;
		output = new ObjectOutputStream(this.socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(this.socket.getInputStream());
		
		username = (String) input.readObject();
		
		server.clients.put(username, output);
		server.outputStreams.put(socket, output);
		
		server.sendToAll("!" + server.clients.keySet());
		
		server.showMessage("\n" + username + "(" + socket.getInetAddress().getHostAddress() + ") is online");
		//starting the thread
		start();
	}
	
	@SuppressWarnings("deprecation")
	public void run(){
		
		try {
			//Thread will run until connections are present
			while(true) {
				try{
					message = input.readObject();
				}catch (Exception e){
					stop();
				}
				
				if (message.toString().contains("@EE@"))
					server.sendToAll(message);
				else {
					String formattedMsg = "@" + username + message.toString().substring(message.toString().indexOf(':'), message.toString().length());
					server.sendPrivately(message.toString().substring(1, message.toString().indexOf(':')), formattedMsg);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				server.removeClient(username);
				server.removeConnection(socket, username);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

