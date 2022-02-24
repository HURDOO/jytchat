package kr.kro.hurdoo.jytchat.config;

import de.beosign.snakeyamlanno.convert.Converter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookieConverter implements Converter<Map<String,String>> {
    @Override
    public String convertToYaml(Map<String, String> map) {
        StringBuilder str = new StringBuilder();
        String[] key = new String[]{"APISID","HSID","LOGIN_INFO","SAPISID","SID","SSID"};
        for(int i=0;i<6;i++) {
            str.append("\n\t").append(key[i]).append(": ").append(map.get(key));
        }
        return str.toString();
    }

    @Override
    public Map<String, String> convertToModel(Object o) {
        Map<String,String> map = new HashMap<>();
        if(o instanceof String) {
            String all = ";" + o + ";";
            String[] list = new String[]{"APISID","HSID","LOGIN_INFO","SAPISID","SID","SSID"};

            for(int i=0;i<6;i++) {
                Pattern pattern = Pattern.compile("[^\\w]" + list[i] + "=([^;]*);");
                Matcher matcher = pattern.matcher(all);
                if(matcher.find()) map.put(list[i],matcher.group(1));
                else System.out.println("Cannot find cookie " + list[i]);
            }
        }
        else map = (Map<String, String>) o;
        return !map.isEmpty() ? map : null;
    }
}
