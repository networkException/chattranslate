package de.nwex.translate.api.web;

public class Parameter
{
    private String key;
    private String value;

    public Parameter(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String format()
    {
        return key + "=" + value;
    }
}
