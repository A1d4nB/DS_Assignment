import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DictionaryServerGUI extends JFrame {

    private JButton startStopButton;
    private JLabel connCountLabel;
    private JTextArea logArea;
    private JLabel ipAddressLabel;
    private JLabel portLabel;
    private static TCPThreadPoolServer server = null;

    public DictionaryServerGUI(int port, String dictionaryFile) {
        int poolSize = 100; // Adjust the pool size as needed
        server = new TCPThreadPoolServer(poolSize, port, dictionaryFile);
        initialiseGUI();
    }

    private void initialiseGUI() {
        setTitle("Dictionary Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panels
        add(createServerStatusPanel(), BorderLayout.NORTH);
        add(createNetworkPanel(), BorderLayout.CENTER);
        add(createLogPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private JPanel createServerStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder("Server Status"));

        startStopButton = new JButton("Stopped");
        connCountLabel = new JLabel("Connection Count: 0 ");
        panel.add(startStopButton, BorderLayout.WEST);
        panel.add(connCountLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createNetworkPanel() {
        String ipAddress = null;
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        //panel.setBorder(new LineBorder(Color.BLACK));

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            ipAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println(e);
        }

        ipAddressLabel = new JLabel("   Server IP Address: " + ipAddress);
        portLabel = new JLabel("Listening on port: " + server.getServerPort()+ "   ");
        panel.add(ipAddressLabel, BorderLayout.WEST);
        panel.add(portLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Server Logs"));

        logArea = new JTextArea(20, 80);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        // TODO: Parse command line arguments
        // Expected: java DictionaryServerGUI.jar <port> <dictionary-file>
        int port = Integer.parseInt(args[0]);
        String dictionaryFile = args[1];



        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Will use default look and feel
            }

            DictionaryServerGUI gui = new DictionaryServerGUI(port, dictionaryFile);
            gui.setVisible(true);

            new Thread(() -> server.start()).start();
            // TODO: Initialize socket connection here
            // gui.setConnectionStatus(true); // Set this when actually connected
        });
    }
}
