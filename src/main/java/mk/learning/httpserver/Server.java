package mk.learning.httpserver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import mk.learning.utilities.Constants;
import mk.learning.utilities.HttpResponse;
import mk.learning.utilities.IOHelperFactory;

public class Server {

	public static Logger logger = Logger.getLogger(Server.class);

	public static String BROADCAST_ADDRESS = "0.0.0.0";

	public static Integer BACKLOG = 10;

	public static String PROPERTY_FILE;

	public static HashMap<String, String> PROPERTIES;

	public static void loadProperties() {
		try {
			List<String> propFileContents = IOHelperFactory.getIOHelper().readFileIntoList(Server.PROPERTY_FILE);
			PROPERTIES = new HashMap<String, String>();
			for (String curProp : propFileContents) {
				String[] temp;
				if (curProp.contains("="))
					temp = curProp.split("=");
				else
					temp = curProp.split(":");
				PROPERTIES.put(temp[0], temp[1]);
			}
			logger.info("successfully loaded properties");
		} catch (IOException e) {
			Server.logger.error(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			Server.logger.error(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	public static String formHttpResponse(String body) {
		HttpResponse response = new HttpResponse();
		if(body == null) {
			response.setHttpStatusCode("404");
			response.setHttpReasonPhrase("Not Found");
		}else {
			response.setHttpStatusCode("200");
			response.setHttpReasonPhrase("OK");
		}
		response.setHttpVersion("HTTP/1.1");
		response.setBody(body);
		response.addHeader("Content-Type", "text/html;charset=UTF-8");
		response.addHeader("Server", "MKS/v1.1");
		//response.addHeader("Connection", "Closed");
		if(body!=null)
			response.addHeader("Content-Length", String.valueOf(body.length()));
		return response.getHttpResponse();
	}
	
	public static Map<String, String> unmarshallRequest(String request) {
		HashMap<String, String> requestMap = new HashMap<String, String>();
		request = request.trim();
		String[] requestLines = request.split("\n");
		String[] lineWords;
		System.out.println("request is : "+request);
		for (int i = 0; i < requestLines.length; i++) {
			switch (i) {
			case 0:
				lineWords = requestLines[i].split("\\s+");
				requestMap.put(Constants.HTTP_METHOD, lineWords[0]);
				requestMap.put(Constants.RESOURCE_PATH, lineWords[1]);
				requestMap.put(Constants.HTTP_VERSION, lineWords[2]);
				break;
			case 1:
				lineWords = requestLines[i].split("\\s+");
				requestMap.put(Constants.HOST, lineWords[1]);
				break;
			case 2:
				lineWords = requestLines[i].split("\\s+");
				requestMap.put(Constants.USER_AGENT, lineWords[1]);
				break;
			case 3:
				lineWords = requestLines[i].split("\\s+");
				requestMap.put(Constants.ACCEPT, lineWords[1]);
				break;
			default:
				logger.error("unable to handle this line - " + requestLines[i]);
				break;
			}
		}
		return requestMap;
	}
	
	public static String getCurrentWorkingDir(){
		return new File("").getAbsolutePath();		
	}
	
	public static File fetchResourceFile(String resourcePath) {
		if (resourcePath.trim().equalsIgnoreCase("/")) {
			Server.logger.info("fetching "+getCurrentWorkingDir()+"/index.html");
			File indexHtmlFile = new File(getCurrentWorkingDir()+"/index.html");
			return (indexHtmlFile.exists() ? indexHtmlFile : null);
		} else {
			File resourceFile = new File(getCurrentWorkingDir()+resourcePath);
			return (resourceFile.exists() ? resourceFile : null);
		}
	}

	public static void handleRequest(Map<String, String> requestMap, Socket curSocket) {
		if (requestMap.get(Constants.HTTP_METHOD).equalsIgnoreCase(Constants.HTTP_GET)) {
			String resourcePath = requestMap.get(Constants.RESOURCE_PATH);
			logger.info("fetching resource: " + resourcePath);
			File resourceFile = fetchResourceFile(resourcePath);
			try {
				if (resourceFile == null) {
					logger.info("resource not found");
					IOHelperFactory.getIOHelper().writeToSocket(curSocket.getOutputStream(), formHttpResponse(null));
				} else {
					logger.info("successfully fetched resource, sending back data");
					String resourceContent = IOHelperFactory.getIOHelper().readFileIntoString(resourceFile.getAbsolutePath());
					IOHelperFactory.getIOHelper().writeToSocket(curSocket.getOutputStream(),
							formHttpResponse(resourceContent));
				}
				//curSocket.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	public static void startServer(int port) {
		try {
			Server.logger.info("starting server at port " + port);
			InetAddress addr = InetAddress.getByName(Server.BROADCAST_ADDRESS);
			ServerSocket serverSocket = new ServerSocket(port, Server.BACKLOG, addr);
			loadProperties();
			Server.logger.info("server successfully started at port " + port);
			Server.logger.info("current working directory is - "+new File("").getAbsolutePath());
			while (true) {
				logger.info("waiting for a connection...");
				Socket curSocket = serverSocket.accept();
				logger.info("recieved connection from " + curSocket.getInetAddress().getHostName());
				String requestContent = IOHelperFactory.getIOHelper().readFromSocket(curSocket.getInputStream());
				if (requestContent == null) {
					Server.logger.error("failed to read from socket");
					curSocket.close();
				} else {
					Map<String, String> requestMap = unmarshallRequest(requestContent);
					handleRequest(requestMap, curSocket);
					if (!curSocket.isClosed())
						curSocket.close();
				}
			}
		} catch (UnknownHostException e) {
			Server.logger.info("unable to start server");
			Server.logger.error(e);
		} catch (IOException e) {
			Server.logger.error(e);
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			Server.logger.error("Wrong usage. Correct command - \"java -jar server.jar <port-no> \"");
			System.exit(1);
		}
		try {
			int port = Integer.parseInt(args[0]);
			if (args.length == 2) {
				Server.PROPERTY_FILE = args[1];
			} else {
				Server.PROPERTY_FILE = "server.properties";
			}
			System.out.println(Server.PROPERTY_FILE);
			Server.startServer(port);
		} catch (NumberFormatException e) {
			Server.logger.error(e);
		}
	}

}
