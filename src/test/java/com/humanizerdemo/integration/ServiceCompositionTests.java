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

    private static TextProcessor text;
    private static DateTimeProcessor dt;

    @BeforeAll
    static void bootstrap() {
        ServiceRegistry reg = new ServiceRegistry();
        text = reg.getTextProcessor();
        dt   = reg.getDateTimeProcessor();
    }

    @Test
    void pipeline_humanizeThenCapitalise_producesTitleCasedOutput() {
        String humanised = text.humanizeIdentifier("orderLineItems");
        String result    = text.convertToTitleCase(humanised);

        assertThat(result).isNotBlank();
        assertThat(Character.isUpperCase(result.charAt(0))).isTrue();
        assertThat(result).contains("Order").contains("Line").contains("Items");
    }

    @Test
    void numberWordsAndOrdinalCompose() {
        String words   = text.convertNumberToWords(3);
        String ordinal = text.formatAsOrdinal(3);
        String label   = text.convertToTitleCase("batch processing run");
        String summary = label + ": " + words.trim() + " items processed (" + ordinal + " run)";

        assertThat(summary).containsIgnoringCase("three");
        assertThat(summary).contains("3rd");
        assertThat(summary).startsWith("Batch");
    }

    @Test
    void pipeline_dateTimeAndTextComposed_producesNotificationMessage() {
        Date base = new Date(1717243200000L);
        Date twoHoursBefore = new Date(base.getTime() - 2L * 3_600_000L);

        String timeDesc  = dt.describeRelativeTo(twoHoursBefore, base);
        String eventName = text.humanizeIdentifier("deploymentTriggered");
        String msg       = eventName + " " + timeDesc + ".";

        assertThat(msg).isNotBlank().endsWith(".");
        assertThat(timeDesc).contains("hour").endsWith("ago");
    }

    @Test
    void byteSizeAndWordCount_storeSummary() {
        String sizeLabel = text.formatByteSize(1024L);
        String inWords   = text.convertNumberToWords(1024L);

        assertThat(sizeLabel).isEqualTo("1 KB");
        assertThat(inWords.trim()).containsIgnoringCase("thousand");
    }

    @Test
    void pipeline_truncateAndHumanize_shortenedOutputIsStillReadable() {
        String humanised  = text.humanizeIdentifier("thisIsAVeryLongCamelCaseIdentifierThatShouldBeTruncated");
        String truncated  = text.truncateToLength(humanised, 20);

        assertThat(truncated.length()).isLessThanOrEqualTo(20);
        assertThat(truncated).isNotBlank();
    }

    @ParameterizedTest(name = "ordinal({0}) ends with a letter")
    @ValueSource(ints = {1, 2, 3, 11, 21, 100})
    void ordinals_allEndWithLetter(int n) {
        String result = text.formatAsOrdinal(n);
        assertThat(result).isNotBlank();
        assertThat(Character.isLetter(result.charAt(result.length() - 1))).isTrue();
    }

    @Test
    void serviceRegistry_servicesNotNull() {
        ServiceRegistry reg = new ServiceRegistry();
        assertThat(reg.getTextProcessor()).isNotNull();
        assertThat(reg.getDateTimeProcessor()).isNotNull();
    }
}
