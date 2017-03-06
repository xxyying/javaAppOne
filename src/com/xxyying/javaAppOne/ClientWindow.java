package com.xxyying.javaAppOne;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable{
	
	private DefaultCaret caret;
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea history;
	
	private Client client;
	private Thread run, listen;
	
	private boolean running = false;
	
	public ClientWindow(String name, String address, int port) {
		setTitle("Chat Client");
		client = new Client(name, address, port);
		boolean connect = client.openConnection(address, port);
		// check the connection status
		if (!connect) {
			System.err.println("Connection failed!");
			console("Connection failed!");
		}
		
		createWindow();
		console("Attempting a connection to " + address + ": " + port + ", user: " + name);
		String connection = "/c/" + name; 
		client.send(connection.getBytes());
		running = true;
		run = new Thread(this, "Running");
		run.start();
	}
	
	
	private void createWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{28, 815, 30, 7}; // sum = 880
		gbl_contentPane.rowHeights = new int[]{35, 475, 40}; // sum = 550
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		JScrollPane scroll = new JScrollPane(history);
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0; // 860
		scrollConstraints.gridy = 0; // 480
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.insets = new Insets(5, 5, 0, 0);
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText());
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = txtMessage.getText();
				console(message);
				txtMessage.setText("");
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		setVisible(true);
		txtMessage.requestFocusInWindow();

	}
	
	public void run() {
		listen();
	}
	
	
	public void send(String message) {
		if (message.equals("")) return;
		message = client.getName() + ": " + message;
		console(message);
		message = "/m/" + message;
		client.send(message.getBytes());
		txtMessage.setText("");
	}
	
	
	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					String message = client.receive();
					if (message.startsWith("/c")) {
						client.setID(Integer.parseInt(message.split("/c/")[1]));
						console("Successfully connected to server! ID: " + client.getID());
					}
				}
			}
		};
		listen.start();
	}
	
	public void console(String message) {
		history.append(message + "\n\r"); // start a new line
		history.setCaretPosition(history.getDocument().getLength());
	}
	
}
