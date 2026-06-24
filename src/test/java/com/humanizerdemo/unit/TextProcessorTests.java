package com.humanizerdemo.unit;

import com.humanizerdemo.service.TextProcessor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class TextProcessorTests {

    private TextProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new TextProcessor();
    }

    @ParameterizedTest(name = "humanize ''{0}'' starts with uppercase")
    @CsvSource({
        "StringHumanizer",
        "canBeCasedToTitle",
        "some_underscored_string"
    })
    void humanizeIdentifier_givenCamelOrUnderscoreInput_returnsCapitalisedReadablePhrase(String input) {
        String result = processor.humanizeIdentifier(input);
        assertThat(result).isNotBlank();
        assertThat(Character.isUpperCase(result.charAt(0))).isTrue();
        assertThat(result).doesNotContain("_");
    }

    @Test
    void humanize_camelCase_insertsSpaces() {
        String result = processor.humanizeIdentifier("helloWorld");
        assertThat(result).containsIgnoringCase("hello");
        assertThat(result).containsIgnoringCase("world");
    }

    @Test
    void humanizeIdentifier_givenBlankInput_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> processor.humanizeIdentifier("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rawIdentifier");
    }

    @Test
    void humanizeIdentifier_givenNullInput_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> processor.humanizeIdentifier(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "titleCase ''{0}'' => ''{1}''")
    @CsvSource({
        "the quick brown fox, The Quick Brown Fox",
        "hello world, Hello World",
        "java unit testing, Java Unit Testing"
    })
    void convertToTitleCase_givenLowercaseSentence_returnsCapitalisedWords(String in, String expected) {
        assertThat(processor.convertToTitleCase(in)).isEqualTo(expected);
    }

    @Test
    void titleCase_blankInput_throws() {
        assertThatThrownBy(() -> processor.convertToTitleCase(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void truncateToLength_givenOverlongText_returnsTextWithinLimit() {
        String result = processor.truncateToLength("This is a very long sentence that should be truncated.", 10);
        assertThat(result.length()).isLessThanOrEqualTo(10);
    }

    @Test
    void truncate_shortText_unchanged() {
        assertThat(processor.truncateToLength("Hi", 100)).isEqualTo("Hi");
    }

    @Test
    void truncate_exactLimit_unchanged() {
        assertThat(processor.truncateToLength("Hello", 5)).isEqualTo("Hello");
    }

    @Test
    void truncateToLength_givenZeroMaxLength_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> processor.truncateToLength("hello", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximumCharacters");
    }

    @Test
    void truncateToLength_givenNullText_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> processor.truncateToLength(null, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "toWords({0}) => ''{1}''")
    @CsvSource({
        "0, zero",
        "1, one",
        "42, forty-two",
        "100, one hundred",
        "1000, one thousand"
    })
    void convertNumberToWords_givenCommonIntegers_returnsEnglishWords(long n, String expected) {
        assertThat(processor.convertNumberToWords(n).trim()).isEqualToIgnoringCase(expected.trim());
    }

    @Test
    void numberToWords_negative_prefixedWithMinus() {
        String result = processor.convertNumberToWords(-5);
        assertThat(result).containsIgnoringCase("minus");
        assertThat(result).containsIgnoringCase("five");
    }

    @ParameterizedTest(name = "ordinal({0}) => ''{1}''")
    @CsvSource({
        "1, 1st",
        "2, 2nd",
        "3, 3rd",
        "4, 4th",
        "11, 11th",
        "21, 21st"
    })
    void formatAsOrdinal_givenPositiveIntegers_returnsCorrectOrdinalSuffix(int n, String expected) {
        assertThat(processor.formatAsOrdinal(n)).isEqualTo(expected.trim());
    }

    @Test
    void formatAsOrdinal_givenNegativeNumber_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> processor.formatAsOrdinal(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positionalNumber");
    }

    @ParameterizedTest(name = "byteSize({0}) => ''{1}''")
    @CsvSource({
        "0, 0 B",
        "1024, 1 KB",
        "1048576, 1 MB"
    })
    void formatByteSize_givenCommonSizes_returnsHumanReadableLabel(long bytes, String expected) {
        assertThat(processor.formatByteSize(bytes)).isEqualTo(expected.trim());
    }

    @Test
    void byteSize_negative_throws() {
        assertThatThrownBy(() -> processor.formatByteSize(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("totalBytes");
    }

    @Test
    void allOutputsNonNull() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(processor.humanizeIdentifier("helloWorld")).isNotNull();
        softly.assertThat(processor.convertToTitleCase("hello world")).isNotNull();
        softly.assertThat(processor.truncateToLength("hello", 3)).isNotNull();
        softly.assertThat(processor.convertNumberToWords(5)).isNotNull();
        softly.assertThat(processor.formatAsOrdinal(5)).isNotNull();
        softly.assertThat(processor.formatByteSize(1024)).isNotNull();
        softly.assertAll();
    }
}
