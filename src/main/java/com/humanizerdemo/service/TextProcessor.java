package com.humanizerdemo.service;

public class TextProcessor {

    private static final String[] UNITS = {
        "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
        "seventeen", "eighteen", "nineteen"
    };

    private static final String[] TENS = {
        "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    public String humanizeIdentifier(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("rawIdentifier must not be null or blank");
        }
        String s = input
            .replaceAll("_", " ")
            .replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
            .toLowerCase()
            .trim();
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public String convertToTitleCase(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            throw new IllegalArgumentException("plainSentence must not be null or blank");
        }
        String[] words = sentence.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
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
        return toWords(n).trim();
    }

    private String toWords(long n) {
        if (n == 0) return "";
        if (n < 20) return UNITS[(int) n] + " ";
        if (n < 100) return TENS[(int) (n / 10)] + (n % 10 != 0 ? "-" + UNITS[(int) (n % 10)] : "") + " ";
        if (n < 1000) return UNITS[(int) (n / 100)] + " hundred " + toWords(n % 100);
        if (n < 1_000_000) return toWords(n / 1000) + "thousand " + toWords(n % 1000);
        if (n < 1_000_000_000) return toWords(n / 1_000_000) + "million " + toWords(n % 1_000_000);
        return toWords(n / 1_000_000_000) + "billion " + toWords(n % 1_000_000_000);
    }

    public String formatAsOrdinal(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("positionalNumber must be zero or greater");
        }
        int mod100 = n % 100;
        int mod10  = n % 10;
        String suffix;
        if (mod100 >= 11 && mod100 <= 13) suffix = "th";
        else if (mod10 == 1) suffix = "st";
        else if (mod10 == 2) suffix = "nd";
        else if (mod10 == 3) suffix = "rd";
        else                  suffix = "th";
        return n + suffix;
    }

    public String formatByteSize(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("totalBytes must be zero or greater");
        }
        if (bytes == 0) return "0 B";
        String[] labels = {"B", "KB", "MB", "GB", "TB"};
        int i = 0;
        double val = bytes;
        while (val >= 1024 && i < labels.length - 1) {
            val /= 1024;
            i++;
        }
        return val == (long) val
            ? (long) val + " " + labels[i]
            : String.format("%.1f %s", val, labels[i]);
    }
}
