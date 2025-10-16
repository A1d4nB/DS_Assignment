import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DictionaryServerGUI extends JFrame {

    private JButton startStopButton;
    private JLabel connCountLabel;
    private int connCount = 0;
    private JTextArea logArea;
    private JLabel ipAddressLabel;
    private JLabel portLabel;
    private static TCPThreadPoolServer server = null;

    public DictionaryServerGUI(int port, String dictionaryFile) {
        int poolSize = 100;

        try {
            server = new TCPThreadPoolServer(poolSize, port, dictionaryFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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

        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(startStopButton.getText().equals("Stopped")) {
                    server.startSocketServer();
                    setConnectionStatus(true);
                } else {
                    server.stopSocketServer();
                    setConnectionStatus(false);
                }
            }
        });

        connCountLabel = new JLabel("Connection Count: 0 ");
        panel.add(startStopButton, BorderLayout.WEST);
        panel.add(connCountLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createNetworkPanel() {
        String ipAddress = null;
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

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

    public int getConnCount() {
        return connCount;
    }

    public void setConnCount(int connCount) {
        this.connCount = connCount;
        connCountLabel.setText("Connection Count: " + connCount);
    }

    public void setConnectionStatus(boolean isConnected) {
        if (isConnected) {
            startStopButton.setText("Running");
        } else {
            startStopButton.setText("Stopped");
        }
    }

    public void logMessage(String message) {
        logArea.append(java.time.LocalTime.now() + ": " + message + "\n");
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Not enough arguments.");
            System.out.println("Usage: java -jar DictionaryServer.jar <server-port> <dictionary-file>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        String dictionaryFile = args[1];


        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {

            }

            DictionaryServerGUI gui = new DictionaryServerGUI(port, dictionaryFile);
            gui.setVisible(true);

            new Thread(() -> server.start(gui)).start();

        });
    }
}
