import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class PrivadoVPN extends JFrame {
    private JTextField txtUser, txtServer;
    private JPasswordField txtPass;
    private JLabel lblStatus;
    private ConfigManager config;
    private ConnectionMonitor monitor;

    public PrivadoVPN() {
        setTitle("Privado VPN Client — Secure Connection");
        setSize(400, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        config = new ConfigManager();
        monitor = new ConnectionMonitor(this);

        // شعار Privado
        ImageIcon logo = new ImageIcon("Privado.png");
        JLabel lblLogo = new JLabel(logo);
        lblLogo.setHorizontalAlignment(JLabel.CENTER);
        add(lblLogo, BorderLayout.NORTH);

        // الحقول
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        txtServer = new JTextField(config.getServer());
        txtUser = new JTextField(config.getUsername());
        txtPass = new JPasswordField(config.getPassword());

        panel.add(new JLabel("Server:"));
        panel.add(txtServer);
        panel.add(new JLabel("Username:"));
        panel.add(txtUser);
        panel.add(new JLabel("Password:"));
        panel.add(txtPass);

        add(panel, BorderLayout.CENTER);

        // الأزرار
        JPanel btnPanel = new JPanel();
        JButton btnConnect = new JButton("Connect");
        JButton btnDisconnect = new JButton("Disconnect");
        btnPanel.add(btnConnect);
        btnPanel.add(btnDisconnect);
        add(btnPanel, BorderLayout.SOUTH);

        // الحالة
        lblStatus = new JLabel("Status: Disconnected", SwingConstants.CENTER);
        add(lblStatus, BorderLayout.PAGE_END);

        // حدث زر الاتصال
        btnConnect.addActionListener(e -> connect());
        btnDisconnect.addActionListener(e -> disconnect());
    }

    private void connect() {
        String server = txtServer.getText();
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        config.saveSettings(server, user, pass);

        try {
            lblStatus.setText("Connecting...");
            runCommand("rasdial \"PrivadoVPN\" " + user + " " + pass);
            lblStatus.setText("Connected to " + server);
            monitor.startMonitoring(server, user, pass);
        } catch (Exception ex) {
            lblStatus.setText("Connection failed!");
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void disconnect() {
        try {
            runCommand("rasdial \"PrivadoVPN\" /disconnect");
            lblStatus.setText("Disconnected");
            monitor.stopMonitoring();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to disconnect: " + ex.getMessage());
        }
    }

    public void updateStatus(String status) {
        lblStatus.setText(status);
    }

    private void runCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("cmd /c " + command);
        process.waitFor();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrivadoVPN().setVisible(true));
    }
}
