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

    private DateTimeProcessor proc;
    private Date ref;

    @BeforeEach
    void setUp() {
        proc = new DateTimeProcessor();
        ref  = new Date(1717243200000L);
    }

    @Test
    void sameDate_returnsJustNow() {
        assertThat(proc.describeRelativeTo(ref, ref)).isEqualTo("just now");
    }

    @Test
    void describeRelativeTo_givenOneHourBeforeReference_returnsAnHourAgo() {
        Date oneHourBefore = new Date(ref.getTime() - 3_600_000L);
        assertThat(proc.describeRelativeTo(oneHourBefore, ref)).isEqualTo("an hour ago");
    }

    @Test
    void threeDaysAgo() {
        Date d = new Date(ref.getTime() - 3L * 86_400_000L);
        assertThat(proc.describeRelativeTo(d, ref)).isEqualTo("3 days ago");
    }

    @Test
    void describeRelativeTo_givenTwoWeeksAfterReference_returnsInTwoWeeks() {
        Date d = new Date(ref.getTime() + 14L * 86_400_000L);
        assertThat(proc.describeRelativeTo(d, ref)).isEqualTo("in 2 weeks");
    }

    @Test
    void describeRelativeTo_givenNullTargetDate_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> proc.describeRelativeTo(null, ref))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetDate");
    }

    @Test
    void nullReference_throws() {
        assertThatThrownBy(() -> proc.describeRelativeTo(ref, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("referenceDate");
    }

    @ParameterizedTest(name = "duration({0}ms) => ''{1}''")
    @CsvSource({
        "60000, a minute",
        "3600000, an hour",
        "86400000, a day"
    })
    void describeDurationInMilliseconds_givenCommonDurations_returnsReadableString(long ms, String expected) {
        assertThat(proc.describeDurationInMilliseconds(ms)).isEqualTo(expected.trim());
    }

    @Test
    void duration_negative_throws() {
        assertThatThrownBy(() -> proc.describeDurationInMilliseconds(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("durationInMilliseconds");
    }
}
