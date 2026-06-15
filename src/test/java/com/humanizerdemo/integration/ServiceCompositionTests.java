package com.humanizerdemo.integration;

import com.humanizerdemo.service.DateTimeProcessor;
import com.humanizerdemo.service.ServiceRegistry;
import com.humanizerdemo.service.TextProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class ServiceCompositionTests {

    private static TextProcessor sharedTextProcessor;
    private static DateTimeProcessor sharedDateTimeProcessor;

    @BeforeAll
    static void bootstrapServiceRegistry() {
        ServiceRegistry sharedServiceRegistry = new ServiceRegistry();
        sharedTextProcessor = sharedServiceRegistry.getTextProcessor();
        sharedDateTimeProcessor = sharedServiceRegistry.getDateTimeProcessor();
    }

    @Test
    void pipeline_humanizeThenCapitalise_producesTitleCasedOutput() {
        String rawIdentifier = "orderLineItems";

        String humanisedPhrase = sharedTextProcessor.humanizeIdentifier(rawIdentifier);
        String titleCasedResult = sharedTextProcessor.convertToTitleCase(humanisedPhrase);

        assertThat(titleCasedResult).isNotBlank();
        assertThat(Character.isUpperCase(titleCasedResult.charAt(0))).isTrue();
        assertThat(titleCasedResult).contains("Order");
        assertThat(titleCasedResult).contains("Line");
        assertThat(titleCasedResult).contains("Items");
    }

    @Test
    void pipeline_numberWordsAndOrdinalComposed_producesMeaningfulSummary() {
        String itemCountInWords = sharedTextProcessor.convertNumberToWords(3);
        String runNumberOrdinal = sharedTextProcessor.formatAsOrdinal(3);
        String batchLabel = sharedTextProcessor.convertToTitleCase("batch processing run");

        String deploymentSummary = batchLabel + ": " + itemCountInWords.trim()
                + " items processed (" + runNumberOrdinal + " run)";

        assertThat(deploymentSummary).containsIgnoringCase("three");
        assertThat(deploymentSummary).contains("3rd");
        assertThat(deploymentSummary).startsWith("Batch");
    }

    @Test
    void pipeline_dateTimeAndTextComposed_producesNotificationMessage() {
        Date referenceDate = new Date(1717243200000L);
        Date twoHoursBeforeReference = new Date(referenceDate.getTime() - 2L * 3_600_000L);

        String relativeTimeDescription = sharedDateTimeProcessor
                .describeRelativeTo(twoHoursBeforeReference, referenceDate);
        String eventLabel = sharedTextProcessor.humanizeIdentifier("deploymentTriggered");

        String notificationMessage = eventLabel + " " + relativeTimeDescription + ".";

        assertThat(notificationMessage).isNotBlank();
        assertThat(notificationMessage).endsWith(".");
        assertThat(relativeTimeDescription).contains("hour");
        assertThat(relativeTimeDescription).endsWith("ago");
    }

    @Test
    void pipeline_byteSizeAndWordCount_produceConsistentStorageSummary() {
        long oneKilobyte = 1024L;

        String humanReadableSizeLabel = sharedTextProcessor.formatByteSize(oneKilobyte);
        String byteCountInWords = sharedTextProcessor.convertNumberToWords(oneKilobyte);

        assertThat(humanReadableSizeLabel).isEqualTo("1 KB");
        assertThat(byteCountInWords.trim()).containsIgnoringCase("thousand");
    }

    @Test
    void pipeline_truncateAndHumanize_shortenedOutputIsStillReadable() {
        String verboseIdentifier = "thisIsAVeryLongCamelCaseIdentifierThatShouldBeTruncated";

        String humanisedVerboseIdentifier = sharedTextProcessor.humanizeIdentifier(verboseIdentifier);
        String truncatedReadableLabel = sharedTextProcessor.truncateToLength(humanisedVerboseIdentifier, 20);

        assertThat(truncatedReadableLabel.length()).isLessThanOrEqualTo(20);
        assertThat(truncatedReadableLabel).isNotBlank();
    }

    @ParameterizedTest(name = "ordinal({0}) ends with a letter")
    @ValueSource(ints = {1, 2, 3, 11, 21, 100})
    void formatAsOrdinal_givenVariousPositiveIntegers_returnsNonBlankOrdinalString(
            int positionalNumber) {
        String ordinalResult = sharedTextProcessor.formatAsOrdinal(positionalNumber);

        assertThat(ordinalResult).isNotBlank();
        char lastCharacterOfOrdinal = ordinalResult.charAt(ordinalResult.length() - 1);
        assertThat(Character.isLetter(lastCharacterOfOrdinal)).isTrue();
    }

    @Test
    void serviceRegistry_allServicesResolveToNonNullInstances() {
        ServiceRegistry freshRegistry = new ServiceRegistry();

        assertThat(freshRegistry.getTextProcessor()).isNotNull();
        assertThat(freshRegistry.getDateTimeProcessor()).isNotNull();
    }
}
