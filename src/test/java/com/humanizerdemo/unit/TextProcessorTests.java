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

    private TextProcessor textProcessorUnderTest;

    @BeforeEach
    void initialiseTextProcessor() {
        textProcessorUnderTest = new TextProcessor();
    }

    @ParameterizedTest(name = "humanize ''{0}'' => ''{1}''")
    @CsvSource({
        "StringHumanizer, String humanizer",
        "canBeCasedToTitle, can be cased to title",
        "some_underscored_string, some underscored string"
    })
    void humanizeIdentifier_givenCamelOrUnderscoreInput_returnsReadablePhrase(
            String rawIdentifier, String expectedPhrase) {
        String actualPhrase = textProcessorUnderTest.humanizeIdentifier(rawIdentifier);
        assertThat(actualPhrase).isEqualToIgnoringCase(expectedPhrase);
    }

    @Test
    void humanizeIdentifier_givenBlankInput_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.humanizeIdentifier("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rawIdentifier");
    }

    @Test
    void humanizeIdentifier_givenNullInput_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.humanizeIdentifier(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "titleCase ''{0}'' => ''{1}''")
    @CsvSource({
        "the quick brown fox, The Quick Brown Fox",
        "hello world, Hello World"
    })
    void convertToTitleCase_givenLowercaseSentence_returnsCapitalisedWords(
            String plainSentence, String expectedTitleCased) {
        String actualResult = textProcessorUnderTest.convertToTitleCase(plainSentence);
        assertThat(actualResult).isEqualToIgnoringCase(expectedTitleCased);
    }

    @Test
    void convertToTitleCase_givenBlankInput_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.convertToTitleCase(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void truncateToLength_givenOverlongText_returnsTextWithinLimit() {
        String longSentence = "This is a very long sentence that should be truncated.";
        String truncatedResult = textProcessorUnderTest.truncateToLength(longSentence, 10);
        assertThat(truncatedResult.length()).isLessThanOrEqualTo(10);
    }

    @Test
    void truncateToLength_givenShortText_returnsOriginalText() {
        String shortWord = "Hi";
        String actualResult = textProcessorUnderTest.truncateToLength(shortWord, 100);
        assertThat(actualResult).isEqualTo(shortWord);
    }

    @Test
    void truncateToLength_givenZeroMaxLength_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.truncateToLength("hello", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximumCharacters");
    }

    @Test
    void truncateToLength_givenNullText_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.truncateToLength(null, 5))
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
    void convertNumberToWords_givenCommonIntegers_returnsEnglishWords(
            long numericValue, String expectedWords) {
        String actualWords = textProcessorUnderTest.convertNumberToWords(numericValue);
        assertThat(actualWords).isEqualToIgnoringCase(expectedWords);
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
    void formatAsOrdinal_givenPositiveIntegers_returnsOrdinalSuffix(
            int positionalNumber, String expectedOrdinal) {
        String actualOrdinal = textProcessorUnderTest.formatAsOrdinal(positionalNumber);
        assertThat(actualOrdinal).isEqualTo(expectedOrdinal);
    }

    @Test
    void formatAsOrdinal_givenNegativeNumber_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.formatAsOrdinal(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positionalNumber");
    }

    @ParameterizedTest(name = "byteSize({0}) contains ''{1}''")
    @CsvSource({
        "0, 0",
        "1024, 1",
        "1048576, 1"
    })
    void formatByteSize_givenCommonSizes_returnsHumanReadableLabel(
            long totalBytes, String expectedFragment) {
        String actualLabel = textProcessorUnderTest.formatByteSize(totalBytes);
        assertThat(actualLabel).contains(expectedFragment);
    }

    @Test
    void formatByteSize_givenNegativeBytes_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.formatByteSize(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("totalBytes");
    }

    @Test
    void softAssert_allTextProcessorOutputsAreNonNull() {
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(textProcessorUnderTest.humanizeIdentifier("helloWorld")).isNotNull();
        softly.assertThat(textProcessorUnderTest.convertToTitleCase("hello world")).isNotNull();
        softly.assertThat(textProcessorUnderTest.truncateToLength("hello", 3)).isNotNull();
        softly.assertThat(textProcessorUnderTest.convertNumberToWords(5)).isNotNull();
        softly.assertThat(textProcessorUnderTest.formatAsOrdinal(5)).isNotNull();
        softly.assertThat(textProcessorUnderTest.formatByteSize(1024)).isNotNull();

        softly.assertAll();
    }
}
