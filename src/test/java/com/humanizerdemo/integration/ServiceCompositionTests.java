package com.humanizerdemo.integration;

import com.humanizerdemo.service.DateTimeProcessor;
import com.humanizerdemo.service.ServiceRegistry;
import com.humanizerdemo.service.TextProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
    void humanizeThenTitleCase_composedCorrectly() {
        String humanised = text.humanizeIdentifier("orderLineItems");
        String result    = text.convertToTitleCase(humanised);

        assertThat(result).contains("Order").contains("Line").contains("Items");
        assertThat(Character.isUpperCase(result.charAt(0))).isTrue();
    }

    @Test
    void numberWordsAndOrdinalCompose() {
        String words   = text.convertNumberToWords(3);
        String ordinal = text.formatAsOrdinal(3);
        String label   = text.convertToTitleCase("batch processing run");
        String summary = label + ": " + words.trim() + " items processed (" + ordinal + " run)";

        assertThat(summary).containsIgnoringCase("three");
        assertThat(summary).contains("3rd").startsWith("Batch");
    }

    @Test
    void dateTimeAndText_notificationMessage() {
        Date base   = new Date(1717243200000L);
        Date before = new Date(base.getTime() - 2L * 3_600_000L);

        String timeDesc = dt.describeRelativeTo(before, base);
        String msg      = text.humanizeIdentifier("deploymentTriggered") + " " + timeDesc + ".";

        assertThat(msg).isNotBlank().endsWith(".");
        assertThat(timeDesc).contains("hour").endsWith("ago");
    }

    @Test
    void serviceRegistry_bothServicesAvailable() {
        ServiceRegistry reg = new ServiceRegistry();
        assertThat(reg.getTextProcessor()).isNotNull();
        assertThat(reg.getDateTimeProcessor()).isNotNull();
    }
}
