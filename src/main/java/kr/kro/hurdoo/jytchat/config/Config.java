package kr.kro.hurdoo.jytchat.config;

import de.beosign.snakeyamlanno.property.YamlProperty;
import kr.kro.hurdoo.jytchat.chat.ChatPermission;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

public class Config {
    public String video_id;
    public boolean enable_chat;

    @YamlProperty(converter = FileConverter.class)
    public File save_file;
    public boolean enable_save;

    @YamlProperty(converter = RegexConverter.class)
    public Pattern check_regex;
    public boolean enable_check;

    @YamlProperty(converter = FileConverter.class)
    public File checklist_file;

    @YamlProperty(converter = CookieConverter.class)
    public Map<String,String> cookies;
    public boolean enable_login;

    @YamlProperty(converter = ChatPermissionConverter.class)
    public ChatPermission chat_limit;
    public String unchecked_message;
    public String non_mod_message;
}
