import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    //public static ServerSocket server;
    //public static Socket socket;
    public static DatagramSocket socket;

    private static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
    private static int clientID;
    private static boolean isRunning;

    public static void start(int port) {

        try {
            //server = new ServerSocket(port);
            socket = new DatagramSocket(port);

            isRunning = true;
            listen();

            System.out.println("Server started on port: " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void broadcast(String message) {
        message = " " + message; //to bring a SPACE before all the message on Client window
        System.out.println("(Server) Messege in broadcast(): " + message);
        if (clients.isEmpty()) {
            System.out.println("(Server) clientTable is empty");
        }
        for (ClientInfo infoClient : clients) {
            System.out.println("(Server) Client info: " + infoClient.getName() + " " + infoClient.getAddress() + " " + infoClient.getPort());
            send(message, infoClient.getAddress(), infoClient.getPort());
        }
    }

    private static void send(String message, InetAddress address, int port) {

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
            System.out.println("Server sends: -------------------------------------------");
            System.out.println("Messege in send() before adding \\e to the end of it in Server Class: " + message);
            message += "\\e";
            System.out.println("Messege in send() before adding \\e to the end of it in Server Class: " + message);
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("(Server) sent message to: (" + address.getHostAddress() + ") (" + port + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void listen() {

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
                        System.out.println("Server listens: -------------------------------------------");
                        System.out.println("(Server) Client says: " + incomingMsg);

                        if (!isCommand(incomingMsg, packet)) {
                            String[] pm = getPersonalMessage(incomingMsg);
                            InetAddress ia = InetAddress.getByName(pm[1]);
                            int port = Integer.parseInt(pm[2]);
                            send(pm[0], ia, port);
                            //broadcast(incomingMsg);
                            //broadcast("PC " + incomingMsg + "\n IP : " + pm[1] + " port : " + pm[2] + " msg: " + pm[0]);
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
     * Server command list:
     * #con:[name] -> connects a client to the server
     * #bc:[name] -> broadcast is enabled
     * #dis:[ID] -> disconnects a client from the server
     */
    private static boolean isCommand(String message, DatagramPacket packet) {

        if (message.startsWith("#con:")) {
            String name = message.substring(message.indexOf(":") + 1);
            ClientInfo ci = new ClientInfo(name, clientID++, packet.getAddress(), packet.getPort());
            System.out.println("(Server) new client info: name: " + name + " ID: " + clientID + " address " + packet.getAddress().toString() + " port: " + packet.getPort());
            clients.add(ci);
            String clientAllInfo = writeBCMessage();
            System.out.println("(Server) string that contains all client info: " + clientAllInfo);

            String nameFL = name.substring(0, 1);
            String nameRst = name.substring(1);

            broadcast("#nc:" + nameFL.toUpperCase() + nameRst.toLowerCase() + " connected!" + clientAllInfo);
            return true;

        } else if (message.startsWith("#bc:")) {
            System.out.println("Messege before trimming #bc: with name in Server Class: " + message);
            message = message.substring(message.indexOf(":") + 1);
            System.out.println("Messege after trimming #bc: and before sending to broadcast() in Server Class: " + message);
            broadcast(message);
            return true;
        }
        return false;
    }

    private static String[] getPersonalMessage(String msg) {
        String[] pm = new String[3];
        String name = msg.substring(0, msg.indexOf(" "));
        String message = msg.substring(msg.indexOf("%") + 1);
        String address = msg.substring(msg.indexOf(" ") + 1, msg.indexOf("@"));
        String port = msg.substring(msg.indexOf("@") + 1, msg.indexOf("%"));

        pm[0] = " " + name + " " + message;
        pm[1] = address;
        pm[2] = port;

        return pm;
    }

    private static String writeBCMessage() {
        String bcMessage = "";
        for(ClientInfo client: clients){
            bcMessage += "%" + client.getName() + "@" + client.getId() + "@" + client.getAddress().getHostName() + "@" + client.getPort();
        }
        return bcMessage;
    }


    public static void stop() {
        isRunning = false;
    }

}
