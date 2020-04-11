package docusearch.utils;

public class StringHelper {
    static public String normalizeString(String text) {
        boolean whitespaceAppended = false, nonWhitespaceFound = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                if (!nonWhitespaceFound) {
                    continue;
                }
                if (!whitespaceAppended) {
                    whitespaceAppended = true;
                    sb.append(c);
                }
            } else {
                nonWhitespaceFound = true;
                whitespaceAppended = false;
                sb.append(c);
            }
        }

        return sb.toString();
    }

    static public boolean characterMatch(char a, char b, boolean isCaseSensitive) {
        if (isCaseSensitive) {
            return a == b;
        } else {
            return Character.toLowerCase(a) == Character.toLowerCase(b);
        }
    }

    static public boolean subStringsMatch(String s1, int start1, String s2, int start2, int length) {
        for (int i = 0; i < length; i++) {
            if (!characterMatch(s1.charAt(start1 + i), s2.charAt(start2 + i), true)) {
                return false;
            }
        }
        return true;
    }
}
