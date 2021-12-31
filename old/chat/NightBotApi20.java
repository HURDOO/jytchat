package old_java.chat;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class NightBotApi20 extends DefaultApi20 {

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.nightbot.tv/oauth2/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://api.nightbot.tv/oauth2/authorize";
    }

    private static class InstanceHolder {
        private static final NightBotApi20 INSTANCE = new NightBotApi20();
    }

    public static NightBotApi20 instance()
    {
        return InstanceHolder.INSTANCE;
    }
}
