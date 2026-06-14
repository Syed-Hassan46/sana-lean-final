package com.humanizerdemo.service;

import com.github.mfornos.humanize.Humanize;

public class TextProcessor {

    public String humanizeIdentifier(String rawIdentifier) {
        if (rawIdentifier == null || rawIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("rawIdentifier must not be null or blank");
        }
        return Humanize.decamelize(rawIdentifier);
    }

    public String convertToTitleCase(String plainSentence) {
        if (plainSentence == null || plainSentence.trim().isEmpty()) {
            throw new IllegalArgumentException("plainSentence must not be null or blank");
        }
        return Humanize.capitalize(plainSentence);
    }

    public String truncateToLength(String fullText, int maximumCharacters) {
        if (fullText == null) {
            throw new IllegalArgumentException("fullText must not be null");
        }
        if (maximumCharacters <= 0) {
            throw new IllegalArgumentException("maximumCharacters must be a positive integer");
        }
        return Humanize.truncate(fullText, maximumCharacters);
    }

    public String convertNumberToWords(long numericValue) {
        return Humanize.spellNumber(numericValue);
    }

    public String formatAsOrdinal(int positionalNumber) {
        if (positionalNumber < 0) {
            throw new IllegalArgumentException("positionalNumber must be zero or greater");
        }
        return Humanize.ordinal(positionalNumber);
    }

    public String formatByteSize(long totalBytes) {
        if (totalBytes < 0) {
            throw new IllegalArgumentException("totalBytes must be zero or greater");
        }
        return Humanize.binaryPrefix(totalBytes);
    }
}
