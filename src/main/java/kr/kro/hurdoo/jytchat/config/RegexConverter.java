package kr.kro.hurdoo.jytchat.config;

import de.beosign.snakeyamlanno.convert.Converter;

import java.util.regex.Pattern;

public class RegexConverter implements Converter<Pattern> {

    @Override
    public String convertToYaml(Pattern pattern) {
        return pattern.pattern();
    }

    @Override
    public Pattern convertToModel(Object o) {
        return Pattern.compile((String) o);
    }
}
