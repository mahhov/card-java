import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class KeyStringer {

    public static void main(String[] arg) throws IOException {
        InputStream streamKey = KeyStringer.class.getResourceAsStream("/encryptionKey128prd.key");
        byte[] bytekey = new byte[streamKey.available()];
        streamKey.read(bytekey);
        String stringKey = new String(Base64.getEncoder().encode(bytekey));
        System.out.println(stringKey);
    }
}
