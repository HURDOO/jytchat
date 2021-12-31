package kr.kro.hurdoo.jytchat.chat;

import kr.kro.hurdoo.jytchat.Jytchat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileSaver {

    private static Thread thread = new Thread(FileSaver::loop);
    private static List<String> chatList = new ArrayList<>();
    private static BufferedWriter writer = null;

    private static boolean doSave = false;

    private static void loop()
    {
        while(doSave)
        {
            try {
                while(!chatList.isEmpty())
                {
                    try {
                        writer.write(chatList.get(0)+"\n");
                        chatList.remove(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(String str)
    {
        chatList.add(str);
    }

    public static void start() throws IOException {
        doSave = true;
        writer = new BufferedWriter(new FileWriter(Jytchat.chatLog));
        thread.start();
    }

    public static void stop()
    {
        doSave = false;
        try {
            if(writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = null;
        thread = new Thread(FileSaver::loop);
    }
}
