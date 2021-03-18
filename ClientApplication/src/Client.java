import com.sun.org.apache.xpath.internal.SourceTree;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;



public class Client {
    public static ArrayList<ClientInfoCS> clientsCS = new ArrayList<ClientInfoCS>();
    private String name;
    private static int clientID;

    private DatagramSocket socket;
    private InetAddress address;
    private int port;





    private boolean isRunning;


    public Client(String name, String address, int port) {

        try {
            this.name = name;
            this.address = InetAddress.getByName(address);
            this.port = port;

            socket = new DatagramSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }

        isRunning = true;
        listen();
        send("#con:" + name);

    }


    public void send(String message) {
        try {

            /*
            Socket socket = new Socket(client.getAddress(), client.getPort());
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            System.out.println("message in Server in send method before writing: " + message);
            oos.writeObject(message);
            System.out.println("message in Server in send method after writing: " + message);

            System.out.println("sent message to " + client.getAddress() + " " + client.getPort());
            */
            System.out.println("Client sends: -------------------------------------------");

            if (message.startsWith("#bc:")) {
                System.out.println("Messege before trimming #bc: in Client Class: " + message);
                message = message.substring(message.indexOf(":") + 1);
                System.out.println("Messege after trimming #bc: in Client Class: " + message);
                message = "#bc:" + name + ": " + message;
                System.out.println("Messege after adding #bc: before message with name in Client Class: " + message);
            } else if (!message.startsWith("#con:")) {
                System.out.println("(Client) sends new connection message " + message);
                message = name + ": " + message;
            }


            message += "\\e";

            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, this.address, port);
            socket.send(packet);
            //System.out.println("(Client) sent message to: (" + address.getHostAddress() + ") (" + port + ")" );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listen() {

        Thread listenThread = new Thread("ChatProgramme listener") {
            public void run() {
                try {
                    while (isRunning) {
                        /*
                        socket = server.accept();
                        System.out.println("Connected to client: (" + socket.getInetAddress() + ") (" + socket.getPort() + ")" );
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        String incomingMsg = (String)ois.readObject();
                        System.out.println("Client says: " + incomingMsg);
                        */
                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);
                        String incomingMsg = new String(data);
                        incomingMsg = incomingMsg.substring(0, incomingMsg.indexOf("\\e"));
                        System.out.println("Client Listens: -------------------------------------------");
                        System.out.println("(Client) (what goes in isCommand() as parameter) Server says: " + incomingMsg);

                        if (!isCommand(incomingMsg)) {
                            ClientWindow.printToConsole(incomingMsg);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listenThread.start();

    }

    /*
    * Client command list:
    * #nc:[name] -> connects a client to the server
    * #dis:[ID] -> disconnects a client from the server
    */
    private static boolean isCommand(String message) {
        try {
            if (message.startsWith(" #nc:")) {
                String clientList = message.substring(message.indexOf("%") + 1);
                System.out.println("(Client) (goes in updateClientList()) client list sent by server: " + clientList);
                String actualMsg = message.substring(0, message.indexOf("!"));
                System.out.println("(Client) actual message for new connection " + actualMsg);
                message = actualMsg.substring(message.indexOf(":") + 1);
                System.out.println("(Client) message for new connection without command keyword" + message);

                updateClientList(clientList);
                ClientWindow.printToOnlineUserCountPanel(clientsCS.size());
                ClientWindow.printToOnlineUserListPanel(clientsCS);

                System.out.println("Message to print in console " + message);
                ClientWindow.printToConsole(" " + message);

                return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateClientList(String clientList) {
        clientsCS.clear();
        System.out.println("(Client) size of ClientsCS arraylist before adding new client to it " + clientsCS.size());
        try{
            String[] clientsInfo = clientList.split("%");

            for(String client: clientsInfo) {
                System.out.println("(Client) individual client information without split " + client);
                String[] clientInfo = client.split("@");
                System.out.println("(Client) Client name " + clientInfo[0]);
                System.out.println("(Client) Client id " + clientInfo[1]);
                System.out.println("(Client) Client address " + clientInfo[2]);
                System.out.println("(Client) Client port " + clientInfo[3]);
                clientsCS.add(new ClientInfoCS(clientInfo[0], Integer.parseInt(clientInfo[1]), InetAddress.getByName(clientInfo[2]), Integer.parseInt(clientInfo[3])));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("(Client) size of ClientsCS arraylist after adding new client to it " + clientsCS.size());

        System.out.println("Online clients: " + clientsCS.size());




        /*
        for (ClientInfoCS client : clientsCS) {

        }



        }

        for (ClientInfoCS client : clientsCS) {
            System.out.println(("name: " + client.getName() + " ClientID: " + client.getId() + " InetAddress: " + client.getAddress() +
                    " port: " + client.getPort()));
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("ClientDB.txt"));
            String line;
            while ((line = reader.readLine()) != null){
                String name = line;

                ClientWindow.userListPanel.add(new JLabel(name));
                line = reader.readLine();
                line = reader.readLine();
                line = reader.readLine();

            }
            reader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
