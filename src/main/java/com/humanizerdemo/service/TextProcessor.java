package com.humanizerdemo.service;

public class TextProcessor {

    private static final String[] ones = {
        "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
        "seventeen", "eighteen", "nineteen"
    };

    private static final String[] tens = {
        "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    public String humanizeIdentifier(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("rawIdentifier must not be null or blank");
        }
        String spaced = input
            .replaceAll("_", " ")
            .replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
            .toLowerCase()
            .trim();
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    public String convertToTitleCase(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            throw new IllegalArgumentException("plainSentence must not be null or blank");
        }
        String[] words = sentence.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String w : words) {
            if (result.length() > 0) result.append(" ");
            result.append(Character.toUpperCase(w.charAt(0)));
            result.append(w.substring(1).toLowerCase());
        }
        return result.toString();
    }

    public String truncateToLength(String text, int maxChars) {
        if (text == null) {
            throw new IllegalArgumentException("fullText must not be null");
        }
        if (maxChars <= 0) {
            throw new IllegalArgumentException("maximumCharacters must be a positive integer");
        }
        if (text.length() <= maxChars) return text;
        if (maxChars <= 3) return text.substring(0, maxChars);
        return text.substring(0, maxChars - 3) + "...";
    }

    public String convertNumberToWords(long n) {
        if (n < 0) return "minus " + convertNumberToWords(-n);
        if (n == 0) return "zero";
        return spellOut(n).trim();
    }

    private String spellOut(long n) {
        if (n == 0) return "";
        if (n < 20) return ones[(int) n] + " ";
        if (n < 100) {
            String t = tens[(int) (n / 10)];
            String u = n % 10 != 0 ? "-" + ones[(int) (n % 10)] : "";
            return t + u + " ";
        }
        if (n < 1000) return ones[(int) (n / 100)] + " hundred " + spellOut(n % 100);
        if (n < 1_000_000) return spellOut(n / 1000) + "thousand " + spellOut(n % 1000);
        if (n < 1_000_000_000) return spellOut(n / 1_000_000) + "million " + spellOut(n % 1_000_000);
        return spellOut(n / 1_000_000_000) + "billion " + spellOut(n % 1_000_000_000);
    }

    public String formatAsOrdinal(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("positionalNumber must be zero or greater");
        }
        int last2 = pos % 100;
        int last1 = pos % 10;
        String suffix;
        if (last2 >= 11 && last2 <= 13) {
            suffix = "th";
        } else if (last1 == 1) {
            suffix = "st";
        } else if (last1 == 2) {
            suffix = "nd";
        } else if (last1 == 3) {
            suffix = "rd";
        } else {
            suffix = "th";
        }
        return pos + suffix;
    }

    public String formatByteSize(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("totalBytes must be zero or greater");
        }
        if (bytes == 0) return "0 B";
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int i = 0;
        double val = bytes;
        while (val >= 1024 && i < units.length - 1) {
            val /= 1024;
            i++;
        }
        if (val == (long) val) return (long) val + " " + units[i];
        return String.format("%.1f %s", val, units[i]);
    }
}
