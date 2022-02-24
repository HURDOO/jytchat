package kr.kro.hurdoo.jytchat.config;

import de.beosign.snakeyamlanno.convert.Converter;

import java.io.File;
import java.io.IOException;

public class FileConverter implements Converter<File> {
    @Override
    public String convertToYaml(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public File convertToModel(Object o) {
        return new File((String) o);
    }
}
