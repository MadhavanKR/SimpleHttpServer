package mk.learning.httpserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

	public static void sendMessage(String host,int port,String data) {
		try {
			Socket cs = new Socket(InetAddress.getByName("127.0.0.1"),8000);
			DataOutputStream dout = new DataOutputStream(cs.getOutputStream());
			dout.writeChars(new String("hello from the other side!!"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client.sendMessage("127.0.0.1", 8000, "hello world");
	}

}
