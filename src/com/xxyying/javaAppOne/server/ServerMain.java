package com.xxyying.javaAppOne.server;

public class ServerMain {
	
	private int port;
	private Server server;
	
	public ServerMain(int port) {
		this.port = port;
		server = new Server(port);
		//System.out.println(port);
	}
	
	public static void main(String[] args) {
		int port;
		if(args.length != 1) {
			System.out.print("Usage: java -jar javaAppOne.jar [port]");
			return;
		}
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}
}
