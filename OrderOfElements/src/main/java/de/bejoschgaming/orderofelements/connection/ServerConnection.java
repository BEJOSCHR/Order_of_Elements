package de.bejoschgaming.orderofelements.connection;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import de.bejoschgaming.orderofelements.animationsystem.AnimationHandler;
import de.bejoschgaming.orderofelements.animationsystem.animations.MenuBookAnimation;
import de.bejoschgaming.orderofelements.componentssystem.TextFieldHandler;
import de.bejoschgaming.orderofelements.debug.ConsoleHandler;
import de.bejoschgaming.orderofelements.filesystem.FileHandler;
import de.bejoschgaming.orderofelements.graphics.DrawState;
import de.bejoschgaming.orderofelements.graphics.GraphicsHandler;
import de.bejoschgaming.orderofelements.graphics.drawparts.Draw_2Login;
import de.bejoschgaming.orderofelements.profile.ClientData;

public class ServerConnection {

	private static String hostname = FileHandler.readOutData(FileHandler.file_Settings, "CONNECTION_IP"); // "ipcwup.no-ip.biz"
	private static int port = Integer.parseInt(FileHandler.readOutData(FileHandler.file_Settings, "CONNECTION_Port"));
	private static int connectionTimeout = Integer.parseInt(FileHandler.readOutData(FileHandler.file_Settings, "CONNECTION_Idletime"))*1000; //IN MS
	public static int maxConnectionTries = 3;
	public static int connectionTry = 0;
	
	private static NioSocketConnector socketConnector;
	private static IoSession serverConnection = null;
	public static boolean connectedToServer = false;
	public static boolean disconnecting = false;
	
	public static List<String> sendPackets = new ArrayList<>();
	
	public static final String packetDivider = FileHandler.readOutData(FileHandler.file_Settings, "CONNECTION_Packetdivider");
	
//	https://mina.apache.org/mina-project/userguide/ch2-basics/ch2.2-sample-tcp-server.html
	
	public static void connectToServer() {
		
		ConsoleHandler.printMessageInConsole("Connecting to server... (Max. tries: "+maxConnectionTries+")", true);
		connectedToServer = false;
		connectionTry = 0;
		
		socketConnector = new NioSocketConnector();
    	socketConnector.setConnectTimeoutMillis(connectionTimeout);
//    	socketConnector.getFilterChain().addLast("logger", new LoggingFilter());
    	socketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
    	socketConnector.setHandler(new ConnectionEventHandler());
		
    	new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				
				connectionTry++;
				
				try {
		        	ConnectFuture future = socketConnector.connect(new InetSocketAddress(hostname, port));
		            future.awaitUninterruptibly();
		            serverConnection = future.getSession();
					this.cancel();
				}catch (RuntimeIoException error) {
//					error.printStackTrace();
					ConsoleHandler.printMessageInConsole("Connecting to server failed! (Try: "+connectionTry+"/"+maxConnectionTries+") ["+error.getMessage()+"]", true);
				}
				
				if(connectionTry == maxConnectionTries) {
					//NOT CONNECTED
					ConsoleHandler.printMessageInConsole("Connecting to server ended with no result! (Try: "+maxConnectionTries+"/"+maxConnectionTries+" failed)", true);
					this.cancel();
					return;
				}
				
			}
		}, 100, 3000);
        
	}
	
	public static void sendPacket(int signal, String message) {
		
		String messageString = signal+packetDivider+message;
		serverConnection.write(messageString);
		sendPackets.add(messageString);
		
	}
	
	public static void handlePacketFromServer(int signal, String message) {
		
		ConsoleHandler.printMessageInConsole(0, "[Received packet] "+signal+" - "+message, true);
		String[] data = message.split(";");
		
		//CLIENT recieve
		switch (signal) {
		case 100:
			//CORRECT LOGIN
			//Syntax: 100-PlayerID;Successfully logged in!
			ClientData.setClientID(Integer.parseInt(data[0]));
			TextFieldHandler.hideTextField(TextFieldHandler.LOGIN_Name);
			TextFieldHandler.hideTextField(TextFieldHandler.LOGIN_Password);
			AnimationHandler.startAnimation(new MenuBookAnimation(true) {
				
				@Override
				protected void halfTimeAction() {
					
					GraphicsHandler.switchTo(DrawState.MENU);
					
				}
				
			});
			ConsoleHandler.printMessageInConsole("Valid login! ClientID: "+ClientData.getClientID()+" - ClientName: "+ClientData.getClientName(), true);
			break;
		case 101:
			//INVALID LOGIN
			//Syntax: 101-Cause
			String invalidCause = message;
			Draw_2Login.loginErrorCause = invalidCause;
			ConsoleHandler.printMessageInConsole("Login invalid! Cause: "+invalidCause, true);
			break;
		}
		
	}
	
	public static boolean checkInputForServerUse(String input) {
		
		if(input.contains(";") || input.contains(":") || input.contains("-") || input.contains("(") || input.contains(")") || input.contains("[") || input.contains("]")) {
			return false;
		}
		
		return true;
		
	}
	
	public static void disconnectFromServer() {
		
		if(disconnecting == true) {
			ConsoleHandler.printMessageInConsole("Already disconnected from server!", true);
			return;
		}
		disconnecting = true;
		
		if(connectedToServer == true) {
			connectedToServer = false;
			serverConnection.closeNow();
			socketConnector.dispose();
			try {
				Thread.sleep(1000*1);
			} catch (InterruptedException error1) {
				error1.printStackTrace();
				ConsoleHandler.printMessageInConsole("Thread interruped failed!", true);
			}
		}else { 
			ConsoleHandler.printMessageInConsole("Can't disconnect, no server connection was established!", true);
		}
		
	}
	
}
