package kr.kro.hurdoo.jytchat.config;

import de.beosign.snakeyamlanno.convert.Converter;
import kr.kro.hurdoo.jytchat.chat.ChatPermission;

public class ChatPermissionConverter implements Converter<ChatPermission> {
    @Override
    public String convertToYaml(ChatPermission permission) {
        return permission.name;
    }

    @Override
    public ChatPermission convertToModel(Object o) {
        switch ((String) o) {
            case "ALL_USERS":
                return ChatPermission.NONE;
            case "CHECKS_ONLY":
                return ChatPermission.CHECK;
            case "MODERATORS_ONLY":
                return ChatPermission.ADMIN;
            default:
                System.out.println("No permission " + o);
                return null;
        }
    }
}
