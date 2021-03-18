import javax.swing.*;
import java.net.InetAddress;

public class RButton extends JRadioButton {
    InetAddress address;
    int port;

    public RButton(String name, InetAddress address, int port) {
        super(name);
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
