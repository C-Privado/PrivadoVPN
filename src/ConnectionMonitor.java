import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConnectionMonitor extends Thread {
    private boolean running = false;
    private PrivadoVPN vpnApp;
    private String server, user, pass;

    public ConnectionMonitor(PrivadoVPN app) {
        this.vpnApp = app;
    }

    public void startMonitoring(String server, String user, String pass) {
        this.server = server;
        this.user = user;
        this.pass = pass;
        running = true;
        this.start();
    }

    public void stopMonitoring() {
        running = false;
    }

    public void run() {
        while (running) {
            try {
                if (!isConnected()) {
                    vpnApp.updateStatus("Reconnecting...");
                    vpnApp.updateStatus("Disconnected — retrying in 10s");
                    Thread.sleep(10000);
                    Runtime.getRuntime().exec("cmd /c rasdial \"PrivadoVPN\" " + user + " " + pass);
                    vpnApp.updateStatus("Reconnected to " + server);
                }
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isConnected() {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c rasdial");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("PrivadoVPN")) return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
