package uj.wmii.pwj.introduction;

public class Reverser {

    public String reverse(String input) {
        if (input == null) return null;
        StringBuilder builder = new StringBuilder(input.trim());
        builder.reverse();
        return builder.toString();
    }

    public String reverseWords(String input) {
        if (input == null) return null;
        String trimmedInput = input.trim();
        String[] words = trimmedInput.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            result.append(words[i]);
            if (i > 0) result.append(" ");
        }
        return result.toString();
    }
}