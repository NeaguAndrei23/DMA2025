package ro.ase.ie.g1106_s04.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnectionService {

    private final String urlAddress;
    private HttpURLConnection httpURLConnection;

    public HttpConnectionService(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public String getData() {
        try {
            return getDataFromHttp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDataFromHttp() throws IOException {
        StringBuilder result = new StringBuilder();
        httpURLConnection = (HttpURLConnection) new URL(urlAddress).openConnection();

        InputStream inputStream = httpURLConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        httpURLConnection.disconnect();

        return result.toString();
    }
}
