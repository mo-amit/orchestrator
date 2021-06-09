package com.ingestyon.orchestrator.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class Utils {


    public static final HashSet<String> validRestMehods = new HashSet<>(Arrays.asList("GET",
            "POST",
            "PUT",
            "HEAD",
            "DELETE",
            "PATCH",
            "OPTIONS"));
    public static String dateFormat = "yyyy-MM-dd HH:mm:ss";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat).withResolverStyle(ResolverStyle.LENIENT);


    public static String getUniqueID(){
        return UUID.randomUUID().toString();
    }


    public static boolean isURLValid(String url)
    {
        URI parsetURI = null;

        try {
            parsetURI = new URL((url.trim())).toURI();
        }
        catch (URISyntaxException exception) {
            return false;
        }
        catch (MalformedURLException exception) {
            return false;
        }

        String protocol = parsetURI.getScheme();
        if ( !protocol.equalsIgnoreCase("http") && !protocol.equalsIgnoreCase("https"))
            return false;
        return true;
    }

    public static boolean isRestMethodValid(String inMethodName){
        if (validRestMehods.contains(inMethodName.trim()))
            return true;
        return false;
    }

    public static boolean isDateValid(String inDateString){
        try {
            LocalDate.parse(inDateString.trim(), dateTimeFormatter);
        } catch (DateTimeParseException ex){
            return false;
        }
        return true;
    }


    public static void main (String[] args){
        isDateValid("2021-05-25 00:17:35");
        isURLValid("https://www.google.com/");
        isRestMethodValid("PUT");

    }
}
