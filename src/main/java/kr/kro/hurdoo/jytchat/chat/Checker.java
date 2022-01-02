package kr.kro.hurdoo.jytchat.chat;

import com.github.kusaanko.youtubelivechat.ChatItem;
import kr.kro.hurdoo.jytchat.Jytchat;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checker {
    private static Thread thread = new Thread(Checker::loop);

    private static List<ChatItem> msg = new LinkedList<>();
    private static final HashMap<String,String> data = new HashMap<>(); // num -> name
    private static final HashMap<String,String> checkedYoutubeID = new HashMap<>(); // school -> youtube
    private static final Pattern pattern = Pattern.compile("!(출석체크|출첵|출쳌) ((\\d{5}) ([가-히]{3,5}))");
    private static boolean doCheck = false;

    private static void loop()
    {
        while(doCheck)
        {
            try {
                while(!msg.isEmpty())
                {
                    ChatItem item = msg.get(0);
                    msg.remove(0);
                    check(item);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void check(ChatItem item)
    {
        String message = item.getMessage();
        Matcher matcher = pattern.matcher(message);
        if(matcher.find()) {
            String info = matcher.group(2);
            String author = item.getAuthorChannelID();
            checkedYoutubeID.put(info,author);
            ChatLimit.checkedSchoolID.put(author,info);

            // @TODO: save to file
        }
    }

    public static void write(ChatItem item)
    {
        msg.add(item);
    }

    public static void start()
    {
        msg.clear();
        try {
            readData();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        doCheck = true;
        thread.start();
    }

    private static void readData() throws IOException {
        data.clear();

        BufferedReader reader = new BufferedReader(new FileReader(Jytchat.studentData));
        String s;
        while((s = reader.readLine()) != null)
        {
            String[] split = s.split(" ");
            if(split.length != 2) return;
            String num = split[0];
            String name = split[1];
            data.put(num,name);
        }
    }

    public static void stop()
    {
        doCheck = false;
        thread = new Thread(Checker::loop);
    }
}
