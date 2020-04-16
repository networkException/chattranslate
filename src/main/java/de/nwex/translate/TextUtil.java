package de.nwex.translate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TextUtil
{
    public static String toMD5(String value)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(value.getBytes());
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(16);
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static String stackTraceToString(Throwable e)
    {
        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;

        do
        {
            if(!first) e = e.getCause();

            for(StackTraceElement element : e.getStackTrace())
            {
                stringBuilder.append(element.toString());
                stringBuilder.append("\n");
            }

            first = false;
        }
        while(e.getCause() != null);

        return stringBuilder.toString();
    }
}
