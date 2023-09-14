package egd.fmre.qslbureau.capture.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtil {
    private static final String USER_AGENT = "Mozilla/5.0";

    public static byte[] httpCall(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            log.error(String.format("Error al consultar la URL (%s): %s", url, responseCode));
            return null;
        }
        InputStream is = null;
        byte[] charArray = null;
        try {
            is = con.getInputStream();
            charArray = FileUtil.inputStreamToByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return charArray;
    }
}
