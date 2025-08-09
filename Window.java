// package graphics;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class Window extends JFrame{

   // All the screen components needed
   JLabel hostPortJLabel, clientPortLabel, connectionStatus, receiverLabel, senderLabel;
   JButton connectionButton, sendMessageButton;
   JTextField hostPortTextField, clientPorttextField, receivedMessageTextField, senderMessageTextField;
   Communication communication;
   Server server;
   Client client;

public Window(String title) {
    super("Local Messenger");
    super.setBounds(200, 200, 1000, 600);
    super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Components
    hostPortJLabel = new JLabel("Host Port: ");
    clientPortLabel = new JLabel("Client Port: ");
    connectionStatus = new JLabel("Empty. Doing Nothing");
    receiverLabel = new JLabel("Them: ");
    senderLabel = new JLabel("You:");

    connectionButton = new JButton("Connect");
   

    sendMessageButton = new JButton("Send");

    hostPortTextField = new JTextField();
    clientPorttextField = new JTextField();
    receivedMessageTextField = new JTextField();
    senderMessageTextField = new JTextField();

    // Layout
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.BOTH; // Allow both horizontal & vertical expansion

    // Row 0: Host Port
    gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; // label stays fixed
    add(hostPortJLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1; // text field stretches
    add(hostPortTextField, gbc);

    // Row 1: Client Port
    gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
    add(clientPortLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1;
    add(clientPorttextField, gbc);

    // Row 2: Connect button (full width)
    gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1;
    add(connectionButton, gbc);

    // Row 3: Connection status (full width)
    gbc.gridy = 3;
    add(connectionStatus, gbc);

    // Row 4: Receiver message
    gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
    add(receiverLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1;
    add(receivedMessageTextField, gbc);

    // Row 5: Sender + send button
    gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
    add(senderLabel, gbc);
    gbc.gridx = 1; gbc.weightx = 1;
    add(senderMessageTextField, gbc);
    gbc.gridx = 2; gbc.weightx = 0;
    add(sendMessageButton, gbc);

    registerButtonForActions();
    super.setVisible(true);
}


public void registerButtonForActions(){
  // communication = new Communication(null, null)
   connectionButton.addActionListener(e -> {
      try {

         connectionStatus.setText("Trying to initialize client and server");
         
         System.out.println("Trying to initialize client and server");
         initializeClientAndServer();
         initiateConnection();
         System.out.println("Initialization Sucessful");

      } catch (NumberFormatException | IOException e1) {
         connectionStatus.setText("Failing to connect");
         System.out.println("problem from...Registering button for action");
      }
   });

  sendMessageButton.addActionListener(e -> {
      System.out.println("Pushing the message to the queue");
      Client.enqueueMessage(senderMessageTextField.getText());
  });
}

public void initializeClientAndServer() throws NumberFormatException, IOException{
   server = new Server(Integer.parseInt(hostPortTextField.getText()), this);
   client = new Client(Integer.parseInt(clientPorttextField.getText()), this);
   communication = new Communication(server, client);
}


public void setReceivedMessageToTextField(String receivedMessage){
   receivedMessageTextField.setText(receivedMessage);
}

public void clearSendMessageOnSucessfulSending(){
   senderMessageTextField.setText("");
}



public String getMessageToSend(){
   return senderMessageTextField.getText();
}


public void initiateConnection(){
   int senderPort = Integer.parseInt(hostPortTextField.getText());
   int receiverPort = Integer.parseInt(clientPorttextField.getText());

   if(communication.initializeConnection()){
      connectionStatus.setText("Connected to client");
   }
   else{
      connectionStatus.setText("Failed to client");
   }
   // establish connection
}

}
