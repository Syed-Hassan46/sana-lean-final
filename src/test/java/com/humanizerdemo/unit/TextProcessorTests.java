package com.humanizerdemo.unit;

import com.humanizerdemo.service.TextProcessor;
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
        "some_underscored_string",
        "helloWorld"
    })
    void humanize_variousFormats_givesReadablePhrase(String input) {
        String result = processor.humanizeIdentifier(input);
        assertThat(result).isNotBlank();
        assertThat(Character.isUpperCase(result.charAt(0))).isTrue();
        assertThat(result).doesNotContain("_");
    }

    @Test
    void humanize_blank_throws() {
        assertThatThrownBy(() -> processor.humanizeIdentifier("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rawIdentifier");
    }

    @ParameterizedTest(name = "''{0}'' => ''{1}''")
    @CsvSource({
        "the quick brown fox, The Quick Brown Fox",
        "hello world, Hello World",
        "java unit testing, Java Unit Testing"
    })
    void titleCase_lowercaseInput_capsEveryWord(String input, String expected) {
        assertThat(processor.convertToTitleCase(input)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "truncate at {1} chars")
    @CsvSource({
        "This is a very long sentence that should be truncated., 10, 10",
        "Hi, 100, 2",
        "Hello, 5, 5"
    })
    void truncate_variousLengths_staysWithinLimit(String input, int maxChars, int expectedMax) {
        assertThat(processor.truncateToLength(input, maxChars).length()).isLessThanOrEqualTo(expectedMax);
    }

    @Test
    void truncate_zeroLimit_throws() {
        assertThatThrownBy(() -> processor.truncateToLength("hello", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximumCharacters");
    }

    @ParameterizedTest(name = "{0} => ''{1}''")
    @CsvSource({
        "0, zero",
        "1, one",
        "42, forty-two",
        "100, one hundred",
        "1000, one thousand"
    })
    void toWords_integers_correctEnglish(long n, String expected) {
        assertThat(processor.convertNumberToWords(n).trim()).isEqualToIgnoringCase(expected.trim());
    }

    @ParameterizedTest(name = "{0} => ''{1}''")
    @CsvSource({
        "1, 1st",
        "2, 2nd",
        "3, 3rd",
        "4, 4th",
        "11, 11th",
        "21, 21st"
    })
    void ordinal_positiveInts_correctSuffix(int n, String expected) {
        assertThat(processor.formatAsOrdinal(n)).isEqualTo(expected.trim());
    }

    @Test
    void ordinal_negative_throws() {
        assertThatThrownBy(() -> processor.formatAsOrdinal(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positionalNumber");
    }

    @ParameterizedTest(name = "{0} bytes => ''{1}''")
    @CsvSource({
        "0, 0 B",
        "1024, 1 KB",
        "1048576, 1 MB"
    })
    void byteSize_commonSizes_readableLabel(long bytes, String expected) {
        assertThat(processor.formatByteSize(bytes)).isEqualTo(expected.trim());
    }

    @Test
    void byteSize_negative_throws() {
        assertThatThrownBy(() -> processor.formatByteSize(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("totalBytes");
    }
}
