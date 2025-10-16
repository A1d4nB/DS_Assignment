import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ~ Dictionary Client GUI Skeleton ~
 * This is a basic GUI for the Dictionary Client.
 * You need to integrate this with your socket communication and
 * protocol implementation.
 *
 * @author [Aidan Freeman Butler    ]
 * Student ID: [1353349]
 */
public class DictionaryClientGUI extends JFrame {

    // GUI Components
    private JTextField wordField;
    private JTextArea meaningArea;
    private JTextField existingMeaningField;
    private JTextField newMeaningField;
    private JTextArea resultArea;
    private JButton searchButton;
    private JButton addWordButton;
    private JButton removeWordButton;
    private JButton addMeaningButton;
    private JButton updateMeaningButton;
    private JLabel statusLabel;
    private static int serverPort;
    private static String serverAddress;
    private static int sleepDuration = 0;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String response = "";

    public DictionaryClientGUI() throws IOException {
        initializeGUI();
    }

    private void initializeGUI() throws IOException {
        setTitle("Dictionary Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panels
        add(createConnectionPanel(), BorderLayout.NORTH);
        add(createOperationsPanel(), BorderLayout.CENTER);
        add(createResultPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(true);

        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.setConnectionStatus(true);
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("Connection Status"));

        statusLabel = new JLabel("Not Connected");
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel);

        return panel;
    }

    private JPanel createOperationsPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Word Panel
        mainPanel.add(createSearchPanel());

        // Add Word Panel
        mainPanel.add(createAddWordPanel());

        // Remove Word Panel
        mainPanel.add(createRemoveWordPanel());

        // Update Operations Panel
        mainPanel.add(createUpdatePanel());

        return mainPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Search Word"));

        wordField = new JTextField();
        searchButton = new JButton("Search");

        panel.add(new JLabel("Word:"), BorderLayout.WEST);
        panel.add(wordField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.EAST);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchWord();
            }
        });

        return panel;
    }

    private JPanel createAddWordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Add New Word"));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField addWordField = new JTextField();
        meaningArea = new JTextArea(3, 20);
        meaningArea.setLineWrap(true);
        meaningArea.setWrapStyleWord(true);
        JScrollPane meaningScroll = new JScrollPane(meaningArea);

        addWordButton = new JButton("Add Word");

        inputPanel.add(new JLabel("Word:"));
        inputPanel.add(addWordField);
        inputPanel.add(new JLabel("Meaning(s):"));
        inputPanel.add(meaningScroll);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addWordButton);

        panel.add(inputPanel, BorderLayout.CENTER);

        addWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addWord(addWordField.getText(), meaningArea.getText());
            }
        });

        return panel;
    }

    private JPanel createRemoveWordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Remove Word"));

        JTextField removeWordField = new JTextField();
        removeWordButton = new JButton("Remove");

        panel.add(new JLabel("Word:"), BorderLayout.WEST);
        panel.add(removeWordField, BorderLayout.CENTER);
        panel.add(removeWordButton, BorderLayout.EAST);

        removeWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeWord(removeWordField.getText());
            }
        });

        return panel;
    }

    private JPanel createUpdatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Update Operations"));

        JPanel operationsPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField updateWordField = new JTextField();
        existingMeaningField = new JTextField();
        newMeaningField = new JTextField();

        addMeaningButton = new JButton("Add Meaning");
        updateMeaningButton = new JButton("Update Meaning");

        operationsPanel.add(new JLabel("Word:"));
        operationsPanel.add(updateWordField);
        operationsPanel.add(new JLabel("Existing Meaning:"));
        operationsPanel.add(existingMeaningField);
        operationsPanel.add(new JLabel("New Meaning:"));
        operationsPanel.add(newMeaningField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addMeaningButton);
        buttonPanel.add(updateMeaningButton);

        panel.add(operationsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addMeaningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMeaning(updateWordField.getText(), newMeaningField.getText());
            }
        });

        updateMeaningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMeaning(updateWordField.getText(),
                        existingMeaningField.getText(),
                        newMeaningField.getText());
            }
        });

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Results"));

        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Methods to be implemented by you

    /**
     * Search for a word in the dictionary
     * You need to implement this
     */
    private void searchWord() {
        String word = wordField.getText().trim();
        if (word.isEmpty()) {
            displayResult("Error: Please enter a word to search.");
            return;
        }

        // TODO: Implement socket communication to server
        // implement timer,
        String message = "QUERY*" + word + "*" + sleepDuration + "*";
        out.println(message);
            try {
                if ((response = in.readLine()) != null) displayResult(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Add a new word with meanings to the dictionary
     * You need to implement this
     */
    private void addWord(String word, String meanings) {
        if (word.trim().isEmpty() || meanings.trim().isEmpty()) {
            displayResult("Error: Both word and meaning(s) are required.");
            return;
        }

        String message = "ADD*" + word + "*" + meanings + "*" + sleepDuration + "*";
        out.println(message);
        try {
            if ((response = in.readLine()) != null) displayResult(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove a word from the dictionary
     * You need to implement this
     */
    private void removeWord(String word) {
        if (word.trim().isEmpty()) {
            displayResult("Error: Please enter a word to remove.");
            return;
        }

        String message = "REMOVE*" + word + "*" + sleepDuration + "*";
        out.println(message);
        try {
            if ((response = in.readLine()) != null) displayResult(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a new meaning to an existing word
     * You need to implement this
     */
    private void addMeaning(String word, String newMeaning) {
        if (word.trim().isEmpty() || newMeaning.trim().isEmpty()) {
            displayResult("Error: Both word and new meaning are required.");
            return;
        }

        String message = "ADDMEANING*" + word + "*" + newMeaning + "*" + sleepDuration + "*";
        out.println(message);
        try {
            if ((response = in.readLine()) != null) displayResult(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update the existing meaning of a word
     * You need to implement this
     */
    private void updateMeaning(String word, String existingMeaning, String newMeaning) {
        if (word.trim().isEmpty() || existingMeaning.trim().isEmpty() || newMeaning.trim().isEmpty()) {
            displayResult("Error: Word, existing meaning, and new meaning are all required.");
            return;
        }

        String message = "UPDATEMEANING*" + word + "*" + existingMeaning + "*" + newMeaning + "*" + sleepDuration + "*";
        out.println(message);
        try {
            if ((response = in.readLine()) != null) displayResult(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Display result in the result area
     */
    private void displayResult(String result) {
        resultArea.append(result + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    /**
     * Update connection status
     * You should call this method when connection status changes
     */
    public void setConnectionStatus(boolean connected) {
        // Connection status
        if (connected) {
            statusLabel.setText("Connected");
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText("Not Connected");
            statusLabel.setForeground(Color.RED);
        }

        // Enable/disable buttons based on connection status
        searchButton.setEnabled(connected);
        addWordButton.setEnabled(connected);
        removeWordButton.setEnabled(connected);
        addMeaningButton.setEnabled(connected);
        updateMeaningButton.setEnabled(connected);
    }

    /**
     * Main method for testing GUI
     * You should modify this to include command line argument parsing
     */
    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Not enough arguments.");
            System.out.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port> <sleep-duration (milliseconds)>");
            System.exit(1);
        }
        serverAddress = args[0];
        serverPort = Integer.parseInt(args[1]);
        sleepDuration = Integer.parseInt(args[2]);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Will use default look and feel
            }

            DictionaryClientGUI gui = null;
            try {
                gui = new DictionaryClientGUI();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            gui.setVisible(true);


        });
    }
}