package de.nwex.translate.api.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request
{
    public static String call(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            System.out.println(url.toString());

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while((inputLine = in.readLine()) != null)
            {
                content.append(inputLine);
            }

            in.close();

            return content.toString();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

        return "";
    }
}
