package com.xxyying.javaAppOne.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
	
	// "hey man"
	// "/c/"
	
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
					process(packet);
					clients.add(new ServerClient("Liu", packet.getAddress(), packet.getPort(), 50));
					System.out.println(clients.get(0).address.toString() + ": " + clients.get(0).port);
//					System.out.println(string);
				}
			}
		};
		receive.start();
		
	}
	
	private void sendToAll(String message) {
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);				
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	
	private void process(DatagramPacket packet) {	
		// /c/Liu
		String string = new String(packet.getData());
		
		if (string.startsWith("/c/")) {
//			UUID id = UUID.randomUUID();
//			System.out.println(id.toString());
			int id = UniqueIdentifier.getIdentifier();
			System.out.println("Identifier: " + id);
			clients.add(new ServerClient(string.substring(3, string.length()), packet.getAddress(), packet.getPort(), id));
			System.out.println(string.substring(3, string.length()));
			String ID = "/c/" + id;
			send(ID.getBytes(), packet.getAddress(), packet.getPort());
		} else if(string.startsWith("/m/")) {
			String message = string.substring(3, string.length());
			sendToAll(message);
		} else {
			System.out.println(string);
		}
	}


	
	
}



























