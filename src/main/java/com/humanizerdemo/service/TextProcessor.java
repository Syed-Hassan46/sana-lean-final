package com.humanizerdemo.service;

public class TextProcessor {

    private static final String[] unitNames = {
        "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
        "seventeen", "eighteen", "nineteen"
    };

    private static final String[] tensNames = {
        "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    private static final String[] ordinalSuffixes = {"th", "st", "nd", "rd"};

    public String humanizeIdentifier(String rawIdentifier) {
        if (rawIdentifier == null || rawIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("rawIdentifier must not be null or blank");
        }
        String withSpaces = rawIdentifier
            .replaceAll("_", " ")
            .replaceAll("([a-z])([A-Z])", "$1 $2")
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
            .toLowerCase()
            .trim();
        return Character.toUpperCase(withSpaces.charAt(0)) + withSpaces.substring(1);
    }

    public String convertToTitleCase(String plainSentence) {
        if (plainSentence == null || plainSentence.trim().isEmpty()) {
            throw new IllegalArgumentException("plainSentence must not be null or blank");
        }
        String[] words = plainSentence.trim().split("\\s+");
        StringBuilder titleCasedResult = new StringBuilder();
        for (String word : words) {
            if (titleCasedResult.length() > 0) {
                titleCasedResult.append(" ");
            }
            titleCasedResult.append(Character.toUpperCase(word.charAt(0)));
            titleCasedResult.append(word.substring(1).toLowerCase());
        }
        return titleCasedResult.toString();
    }

    public String truncateToLength(String fullText, int maximumCharacters) {
        if (fullText == null) {
            throw new IllegalArgumentException("fullText must not be null");
        }
        if (maximumCharacters <= 0) {
            throw new IllegalArgumentException("maximumCharacters must be a positive integer");
        }
        if (fullText.length() <= maximumCharacters) {
            return fullText;
        }
        if (maximumCharacters <= 3) {
            return fullText.substring(0, maximumCharacters);
        }
        return fullText.substring(0, maximumCharacters - 3) + "...";
    }

    public String convertNumberToWords(long numericValue) {
        if (numericValue < 0) {
            return "minus " + convertNumberToWords(-numericValue);
        }
        if (numericValue == 0) {
            return "zero";
        }
        return convertBelowThousand(numericValue).trim();
    }

    private String convertBelowThousand(long numericValue) {
        if (numericValue == 0) {
            return "";
        }
        if (numericValue < 20) {
            return unitNames[(int) numericValue] + " ";
        }
        if (numericValue < 100) {
            return tensNames[(int) (numericValue / 10)]
                + (numericValue % 10 != 0 ? "-" + unitNames[(int) (numericValue % 10)] : "")
                + " ";
        }
        if (numericValue < 1000) {
            return unitNames[(int) (numericValue / 100)] + " hundred "
                + convertBelowThousand(numericValue % 100);
        }
        if (numericValue < 1_000_000) {
            return convertBelowThousand(numericValue / 1000) + "thousand "
                + convertBelowThousand(numericValue % 1000);
        }
        if (numericValue < 1_000_000_000) {
            return convertBelowThousand(numericValue / 1_000_000) + "million "
                + convertBelowThousand(numericValue % 1_000_000);
        }
        return convertBelowThousand(numericValue / 1_000_000_000) + "billion "
            + convertBelowThousand(numericValue % 1_000_000_000);
    }

    public String formatAsOrdinal(int positionalNumber) {
        if (positionalNumber < 0) {
            throw new IllegalArgumentException("positionalNumber must be zero or greater");
        }
        int lastTwoDigits = positionalNumber % 100;
        int lastDigit = positionalNumber % 10;
        String suffix;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 13) {
            suffix = "th";
        } else if (lastDigit == 1) {
            suffix = "st";
        } else if (lastDigit == 2) {
            suffix = "nd";
        } else if (lastDigit == 3) {
            suffix = "rd";
        } else {
            suffix = "th";
        }
        return positionalNumber + suffix;
    }

    public String formatByteSize(long totalBytes) {
        if (totalBytes < 0) {
            throw new IllegalArgumentException("totalBytes must be zero or greater");
        }
        if (totalBytes == 0) {
            return "0 B";
        }
        String[] unitLabels = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double scaledValue = totalBytes;
        while (scaledValue >= 1024 && unitIndex < unitLabels.length - 1) {
            scaledValue /= 1024;
            unitIndex++;
        }
        if (scaledValue == (long) scaledValue) {
            return (long) scaledValue + " " + unitLabels[unitIndex];
        }
        return String.format("%.1f %s", scaledValue, unitLabels[unitIndex]);
    }
}
