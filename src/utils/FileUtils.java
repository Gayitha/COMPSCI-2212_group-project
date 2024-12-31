package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {
    private static final String USER_DICTIONARY = "user_dictionary.txt";
    public static String read(String path) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static Set<String> readWords() throws Exception{
        Set<String> words = new HashSet<>();

        File file = new File(USER_DICTIONARY);
        if (!file.exists()) {
            file.createNewFile();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(USER_DICTIONARY))));
        String line;
        while ((line = br.readLine()) != null) {
            words.add(line);
        }
        return words;
    }

    public static void write( Set<String> userDictionary) throws Exception{
        StringBuilder sb = new StringBuilder();
        for (String s : userDictionary) {
            sb.append(s).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        Files.write(Paths.get(USER_DICTIONARY), sb.toString().getBytes());
    }

    public static void write(String path, String text) throws Exception{
        Files.write(Paths.get(path), text.getBytes());
    }
}
