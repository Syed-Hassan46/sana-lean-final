package com.humanizerdemo.unit;

import com.humanizerdemo.service.DateTimeProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class DateTimeProcessorTests {

    private DateTimeProcessor dateTimeProcessorUnderTest;
    private Date fixedReferenceDate;

    @BeforeEach
    void initialise() {
        dateTimeProcessorUnderTest = new DateTimeProcessor();
        fixedReferenceDate = new Date(1717243200000L);
    }

    @Test
    void describeRelativeTo_givenSameDate_returnsNowDescription() {
        String relativeDescription = dateTimeProcessorUnderTest
                .describeRelativeTo(fixedReferenceDate, fixedReferenceDate);
        assertThat(relativeDescription).containsIgnoringCase("now");
    }

    @Test
    void describeRelativeTo_givenOneHourBeforeReference_returnsAnHourAgoDescription() {
        Date oneHourBeforeReference = new Date(fixedReferenceDate.getTime() - 3_600_000L);
        String relativeDescription = dateTimeProcessorUnderTest
                .describeRelativeTo(oneHourBeforeReference, fixedReferenceDate);
        assertThat(relativeDescription).containsIgnoringCase("hour");
    }

    @Test
    void describeRelativeTo_givenThreeDaysBeforeReference_returnsDaysAgoDescription() {
        Date threeDaysBeforeReference = new Date(fixedReferenceDate.getTime() - 3L * 86_400_000L);
        String relativeDescription = dateTimeProcessorUnderTest
                .describeRelativeTo(threeDaysBeforeReference, fixedReferenceDate);
        assertThat(relativeDescription).containsIgnoringCase("day");
    }

    @Test
    void describeRelativeTo_givenTwoWeeksAfterReference_returnsFutureDescription() {
        Date twoWeeksAfterReference = new Date(fixedReferenceDate.getTime() + 14L * 86_400_000L);
        String relativeDescription = dateTimeProcessorUnderTest
                .describeRelativeTo(twoWeeksAfterReference, fixedReferenceDate);
        assertThat(relativeDescription).isNotBlank();
    }

    @Test
    void describeRelativeTo_givenNullTargetDate_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> dateTimeProcessorUnderTest
                .describeRelativeTo(null, fixedReferenceDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetDate");
    }

    @Test
    void describeRelativeTo_givenNullReferenceDate_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> dateTimeProcessorUnderTest
                .describeRelativeTo(fixedReferenceDate, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("referenceDate");
    }

    @ParameterizedTest(name = "duration({0}ms) contains ''{1}''")
    @CsvSource({
        "60000,   minute",
        "3600000, hour",
        "86400000, day"
    })
    void describeDurationInMilliseconds_givenCommonDurations_returnsReadableString(
            long durationInMilliseconds, String expectedFragment) {
        String durationDescription = dateTimeProcessorUnderTest
                .describeDurationInMilliseconds(durationInMilliseconds);
        assertThat(durationDescription).containsIgnoringCase(expectedFragment);
    }

    @Test
    void describeDurationInMilliseconds_givenNegativeValue_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> dateTimeProcessorUnderTest.describeDurationInMilliseconds(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("durationInMilliseconds");
    }
}
