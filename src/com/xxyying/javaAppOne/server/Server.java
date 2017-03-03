package com.xxyying.javaAppOne.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	
	private int port;
	private DatagramSocket socket;
	private boolean running = false;
	private Thread run, manage, send, receive;

	
	
	public Server(int port) { // constructor
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server");
		run.start();
	}
	
	public void run() {
		running = true;
		System.out.println("Sever start on port: " + port);
		manageClients();
		receive();
	}

	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					// Managing
					
				}
			}
		};
		manage.start();
	}
	
	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					// Receiving
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
//						packet.getAddress();
//						packet.getPort();
					} catch (IOException e) {
						e.printStackTrace();
					}
					String string = new String(packet.getData());
					clients.add(new ServerClient("Liu", packet.getAddress(), packet.getPort(), 50));
					System.out.println(clients.get(0).address.toString() + ": " + clients.get(0).port);
					
					System.out.println(string);
				}
			}
		};
		receive.start();
		
	}
	
}
















