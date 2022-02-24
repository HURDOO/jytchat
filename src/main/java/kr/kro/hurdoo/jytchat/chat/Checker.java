package kr.kro.hurdoo.jytchat.chat;

import com.github.kusaanko.youtubelivechat.ChatItem;
import kr.kro.hurdoo.jytchat.Jytchat;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checker {
    private static Thread thread = new Thread(Checker::loop);

    private static final List<ChatItem> msg = new LinkedList<>();
    private static final HashSet<String> studentList = new HashSet<>();
    private static final HashMap<String,String> checkedYoutubeID = new HashMap<>(); // school -> youtube
    private static Pattern pattern = null;
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
        if(!doCheck) return;
        String message = item.getMessage();
        Matcher matcher = pattern.matcher(message);
        if(matcher.find()) {
            String info = matcher.group(2);
            String author = item.getAuthorChannelID();

            // if same youtube id
            if(checkedYoutubeID.containsKey(info)) {
                ChatLimit.checkedSchoolID.remove(checkedYoutubeID.get(info));
                checkedYoutubeID.remove(author);
            }
            // if same student info
            if(ChatLimit.checkedSchoolID.containsKey(author)) {
                checkedYoutubeID.remove(ChatLimit.checkedSchoolID.get(author));
                ChatLimit.checkedSchoolID.remove(author);
            }
            // check if on student list
            if(!studentList.isEmpty()) {
                if(!studentList.contains(info)) {
                    System.out.printf("Cannot find student %s\n", info);
                    return;
                }
            }

            checkedYoutubeID.remove(info);
            ChatLimit.checkedSchoolID.remove(author);
            checkedYoutubeID.put(info,author);
            ChatLimit.checkedSchoolID.put(author,info);

            // @TODO: save to file
            System.out.printf("%s checked for %s\n", info, author);
        }
    }

    public static void start()
    {
        msg.clear();
        try {
            readData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doCheck = true;
        thread.start();
    }

    public static void setRegexPattern(Pattern pattern) {
        Checker.pattern = pattern;
    }

    private static void readData() throws IOException {
        studentList.clear();

        BufferedReader reader = new BufferedReader(new FileReader(Jytchat.studentData));
        String s;
        while((s = reader.readLine()) != null)
        {
            studentList.add(s);
            System.out.println("Read student " + s);
        }
    }

    public static void stop()
    {
        doCheck = false;
        thread = new Thread(Checker::loop);
        studentList.clear();
        checkedYoutubeID.clear();
        ChatLimit.checkedSchoolID.clear();
    }
}
