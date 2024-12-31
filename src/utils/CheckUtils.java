package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {
    public static final String WORDS_FILE = "words_alpha.txt";
    private static final String INSERT_STR = "abcdefghijklmnopqrstuvwxyz";
    private final static Set<String> WORD_DATA = new HashSet<>();

    public static final String REGEX = "([\\p{Punct}\\s]+)";

    public static void initWordData() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(WORDS_FILE))));
        String line;
        while ((line = br.readLine()) != null) {
            WORD_DATA.add(line);
        }
    }

    private static Set<String> wordEdit(String word) {
        char[] c = INSERT_STR.toCharArray();
        int n = word.length();
        int m = INSERT_STR.length();
        Set<String> set = new HashSet<>();

        if (word.equals("dr")|| word.equals("mr")|| word.equals("mrs")|| word.equals("ms")|| word.equals("jr")|| word.equals("sr")){
            set.add(word.substring(0,1).toUpperCase()+word.substring(1).toLowerCase());
            set.add(word.toUpperCase());
            return set;
        }

        // deletion
        for (int i = 0; i < n; i++) {
            String word1 = word.substring(0, i) + word.substring(i + 1, n);
            set.add(word1);
        }
        // transposition
        for (int i = 0; i < n - 1; i++) {
            String word1 = word.substring(0, i) + word.charAt(i + 1) +
                    word.charAt(i) + word.substring(i + 2, n);
            set.add(word1);
        }
        // alteration
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                String word1 = word.substring(0, i) + c[j] + word.substring(i + 1, n);
                set.add(word1);
            }
        }
        // insertion
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                String word1 = word.substring(0, i) + c[j] + word.substring(i, n);
                set.add(word1);
            }
        }
        // append
        for (int j = 0; j < m; j++) {
            set.add(word + c[j]);
        }
        // space
        for (int i = 0; i < n - 1; i++) {
            String word1 = word.substring(0, i) + " " + word.substring(i, n);
            set.add(word1);
        }
        return set;
    }

    public static Set<String> getAllPossibleWords(String word) {
        Set<String> set = new HashSet<>();
        for (String w : wordEdit(word)) {
            String[] split = w.split(" ");
            if (split.length == 1) {
                if (WORD_DATA.contains(w)) {
                    set.add(w);
                }
            } else {
                boolean flag = true;
                for (String s : split) {
                    if (!WORD_DATA.contains(s)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    set.add(w);
                }
            }
        }
        return set;
    }

    public static boolean check(String word) {
        if (word.matches("\\d+")){
            return true;
        }
        if (word.equals(word.toUpperCase())){
            return true;
        }
        if (word.equals("dr")|| word.equals("mr")|| word.equals("mrs")|| word.equals("ms")|| word.equals("jr")|| word.equals("sr")){
            return false;
        }
        return WORD_DATA.contains(word.toLowerCase());
    }

    public static boolean checkUpperHead(String word) {
        if (word.isEmpty()){
            return true;
        }
        return word.charAt(0) >= 'A' && word.charAt(0) <= 'Z';
    }

    public static Set<String> correctionUpperHead(String word) {
        Set<String> set = new HashSet<>();
        word = word.substring(0,1).toUpperCase()+word.substring(1).toLowerCase();
        set.add(word);
        return set;
    }

    public static boolean checkMixUpper(String word) {
        if (word.isEmpty()){
            return true;
        }
        if (word.equals("Dr")|| word.equals("Mr")|| word.equals("Mrs")|| word.equals("Ms")|| word.equals("Jr")|| word.equals("Sr")){
            return true;
        }
        char[] chars = word.toCharArray();
        boolean hasUpper = false;
        boolean hasLower = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'A' && chars[i] <= 'Z') {
                hasUpper = true;
            }
            if (chars[i] >= 'a' && chars[i] <= 'z') {
                hasLower = true;
            }
        }
        return !(hasUpper && hasLower) || word.equals(word.toUpperCase()) || word.equals(word.toLowerCase());
    }

    public static Set<String> correctionMixUpper(String word) {
        Set<String> set = new HashSet<>();
        set.add(word.toLowerCase());
        set.add(word.toUpperCase());
        set.add(word.substring(0,1).toUpperCase()+word.substring(1).toLowerCase());

        return set;
    }


    public static boolean checkDoubleWord(String str){
        String[] split = str.split(REGEX);
        for (int i = 0; i < split.length - 1; i++) {
            if (split[i].equalsIgnoreCase(split[i + 1])) {
                return false;
            }
        }
        return true;
    }

    public static String deleteDoubleWord(String str){
        String[] split = str.split(REGEX);
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(str);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            if (!split[i].equalsIgnoreCase(split[i + 1])) {
                sb.append(split[i]);
                if (matcher.find()) {
                    sb.append(matcher.group());
                }
            }
        }
        sb.append(split[split.length - 1]);
        return sb.toString();
    }

    public static List<String> splitLabel(String str){
        String pattern = "<.*?>";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(str);

        List<String> result = new ArrayList<>();

        String[] split = str.split(pattern);
        for (int i = 0; i < split.length; i++) {
            if (!split[i].isEmpty()){
                result.add(split[i]);
            }

            if (matcher.find()) {
                result.add(matcher.group());
            }
        }

        return result;
    }

    public static String removeLabel(String str){
        String pattern = "<.*?>";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(str);
        return matcher.replaceAll("");
    }

}
