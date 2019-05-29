package mk.learning.utilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import mk.learning.httpserver.Server;

public class IOHelper {

	public String readFromSocket(InputStream in) {
		DataInputStream din = new DataInputStream(in);
		StringBuilder out = new StringBuilder();
		String line;
		try {
			byte[] b = new byte[1024];
			int iter = 0;
			int readlen=0;
			while (true) {
				readlen=in.read(b);
				String curLine = new String(b);
				out.append(curLine + "\n");
				if(in.available()==0)
					break;
			}
			System.out.println("read from socket.. "+out.toString());
			return out.toString();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("returning null....");
		return null;
	}

	public boolean writeToSocket(OutputStream out, String data) {
		DataOutputStream dout = new DataOutputStream(out);
		try {
			//dout.writeChars(data);
			dout.writeUTF(data);
			dout.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public List<String> readFileIntoList(String filePath) throws FileNotFoundException,IOException {
		ArrayList<String> result = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		while((line=reader.readLine())!=null)
			result.add(line);
		return result;
	}
	
	public String readFileIntoString(String filePath) throws FileNotFoundException,IOException {
		Server.logger.info("reading resource: "+filePath);
		StringBuilder result = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		while((line=reader.readLine())!=null)
			result.append(line);
		return result.toString();
	}
	
}
