import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class ConfigManager {
    private final String configFile = "config.properties";
    private final String secretKey = "PrivadoAESKey16"; // يجب أن تكون 16 بايت

    public void saveSettings(String server, String user, String password) {
        try {
            Properties props = new Properties();
            props.setProperty("server", server);
            props.setProperty("user", user);
            props.setProperty("password", encrypt(password));
            FileOutputStream out = new FileOutputStream(configFile);
            props.store(out, null);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServer() {
        return getProperty("server", "vpn.al-sebaee.com");
    }

    public String getUsername() {
        return getProperty("user", "");
    }

    public String getPassword() {
        String enc = getProperty("password", "");
        return enc.isEmpty() ? "" : decrypt(enc);
    }

    private String getProperty(String key, String def) {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(configFile);
            props.load(in);
            in.close();
            return props.getProperty(key, def);
        } catch (Exception e) {
            return def;
        }
    }

    private String encrypt(String strToEncrypt) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }

    private String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            return "";
        }
    }
}
