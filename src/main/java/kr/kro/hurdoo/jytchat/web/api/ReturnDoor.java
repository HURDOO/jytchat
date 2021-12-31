package kr.kro.hurdoo.jytchat.web.api;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import kr.kro.hurdoo.jytchat.Log;
import kr.kro.hurdoo.jytchat.Test;
import kr.kro.hurdoo.jytchat.web.GoogleAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReturnDoor {
    public static void done(ReturnType type, Object result)
    {
        list.add(new Respond(type,result));
    }

    private final static List<Respond> list = new ArrayList<>();

    private static class Respond {
        public ReturnType type;
        public Object result;
        public Respond(ReturnType type, Object result) {
            this.type = type;
            this.result = result;
        }
    }

    public static Timeline timeline;
    public static void start()
    {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1),event -> loop()));
        timeline.play();
    }

    public static void loop() {
        while(!list.isEmpty()) {
            Respond item = list.get(0);
            list.remove(0);
            switch(item.type)
            {
                case GOOGLE_AUTH_USER:
                    if(!(item.result instanceof String)) throw new IllegalArgumentException("return is not String");
                    GoogleAuth.instance.parseCode(item.result.toString());
                    GoogleAuth.instance.startTokenRequest();
                    break;
                case GOOGLE_AUTH_SERVER:
                    GoogleAuth.instance.parseAccessToken(item.result.toString());
                    System.out.println(GoogleAuth.instance.access_token);
                    break;
                default:
                    Log.log(Level.SEVERE,"Nothing is defined on type " + item.type);
                    break;
            }
        }
    }
}
