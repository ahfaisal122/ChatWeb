import java.awt.EventQueue;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.util.ArrayList;

public class ClientWindow {

    private JFrame frameMain;
    private JTextField messageField;
    private JRadioButton rdbtnBroadcast;
    private static JTextArea textArea = new JTextArea();
    private static JPanel onlineUserCountPanel;
    private static JLabel onlineUserCount = new JLabel();
    private static JPanel userListPanel;
    private static RButton radios;
    private static ButtonGroup bg = new ButtonGroup();;
    private static ArrayList<RButton> rButtons = new ArrayList<RButton>();

    private Client client;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    ClientWindow window = new ClientWindow();
                    window.frameMain.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public ClientWindow() {
        initialize();

        String name = JOptionPane.showInputDialog("Enter NickName");
        String nameFL = name.substring(0,1);
        String nameRst = name.substring(1);
        frameMain.setTitle(nameFL.toUpperCase() + nameRst.toLowerCase()); //to set the title of the Client window to Client's name

        client = new Client(name, "localhost", 5054);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frameMain = new JFrame();
        frameMain.setResizable(false);
        frameMain.setTitle("Java Messanger");
        frameMain.setBounds(100, 100, 590, 525);
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameMain.getContentPane().setLayout(new BorderLayout(0, 0));
        textArea.setFont(new Font("Optima", Font.BOLD, 18));
        textArea.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.LIGHT_GRAY));

        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        frameMain.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
        frameMain.getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        messageField = new JTextField();
        messageField.setFont(new Font("Courier", Font.PLAIN, 13));
        panel.add(messageField);
        messageField.setColumns(30);

        JButton buttonSend = new JButton("Send");
        buttonSend.setFont(new Font("Courier", Font.PLAIN, 14));
        buttonSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(!messageField.getText().equals("")) {
                    String message = messageField.getText();
                    System.out.println("The messege is " + message);
                    if(rdbtnBroadcast.isSelected()) {
                        message = "#bc:" + message;
                    } else {
                        for(RButton rb: rButtons) {
                            if(rb.isSelected()) {
                                message = rb.getAddress().getHostName() + "@" + rb.getPort() + "%" + message;
                                break;
                            }
                        }
                    }
                    System.out.println("Messege from messageField: " + message);
                    client.send(message);
                    messageField.setText("");
                }
            }
        });
        panel.add(buttonSend);

        JPanel listPanel = new JPanel();
        listPanel.setBackground(Color.LIGHT_GRAY);
        frameMain.getContentPane().add(listPanel, BorderLayout.WEST);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        onlineUserCountPanel = new JPanel();
        onlineUserCountPanel.setBackground(Color.LIGHT_GRAY);
        onlineUserCountPanel.setBackground(Color.LIGHT_GRAY);
        onlineUserCountPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        onlineUserCountPanel.setBorder(new EmptyBorder(8, 7, 8, 7));
        listPanel.add(onlineUserCountPanel);
        onlineUserCountPanel.setLayout(new BoxLayout(onlineUserCountPanel, BoxLayout.Y_AXIS));


        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.LIGHT_GRAY);
        panel_1.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        listPanel.add(panel_1);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

        JScrollPane scrollPane_1 = new JScrollPane();

        panel_1.add(scrollPane_1);

        userListPanel = new JPanel();
        userListPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        scrollPane_1.setViewportView(userListPanel);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));






        rdbtnBroadcast = new JRadioButton("Broadcast");
        bg.add(rdbtnBroadcast);
        rdbtnBroadcast.setFont(new Font("Courier", Font.PLAIN, 14));
        rdbtnBroadcast.setBorder(new EmptyBorder(7, 7, 8, 7));
        rdbtnBroadcast.setHorizontalAlignment(SwingConstants.CENTER);
        rdbtnBroadcast.setAlignmentY(Component.TOP_ALIGNMENT);
        rdbtnBroadcast.setBackground(Color.LIGHT_GRAY);
        rdbtnBroadcast.setAlignmentX(Component.CENTER_ALIGNMENT);
        rdbtnBroadcast.setVerticalAlignment(SwingConstants.TOP);
        rdbtnBroadcast.setSelected(true);
        listPanel.add(rdbtnBroadcast);

        frameMain.setLocationRelativeTo(null);

    }

    public static void printToConsole(String message) {
        textArea.setText(textArea.getText() + message + "\n");
    }

    public static void printToOnlineUserCountPanel(int count) {
        //onlineUserCountPanel.removeAll();
        onlineUserCount.setText(" Online user: " + count + " ");
        onlineUserCount.setFont(new Font("Courier", Font.PLAIN, 14));
        onlineUserCount.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        onlineUserCountPanel.add(onlineUserCount);
        //userListPanel.revalidate();
        //userListPanel.repaint();
    }


    public static void printToOnlineUserListPanel(ArrayList<ClientInfoCS> clients ) {

        rButtons.clear();
        for(ClientInfoCS client: clients) {
            radios = new RButton(client.getName(), client.getAddress(), client.getPort());
            rButtons.add(radios);
        }

        userListPanel.removeAll();
        for(RButton rb: rButtons){
            bg.add(rb);
            userListPanel.add(rb);

            userListPanel.revalidate();
            userListPanel.repaint();
        }



    }

}
