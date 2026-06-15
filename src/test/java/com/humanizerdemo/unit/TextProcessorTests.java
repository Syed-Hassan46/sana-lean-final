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

    @ParameterizedTest(name = "humanize ''{0}'' starts with uppercase")
    @CsvSource({
        "StringHumanizer",
        "canBeCasedToTitle",
        "some_underscored_string"
    })
    void humanizeIdentifier_givenCamelOrUnderscoreInput_returnsCapitalisedReadablePhrase(
            String rawIdentifier) {
        String actualPhrase = textProcessorUnderTest.humanizeIdentifier(rawIdentifier);
        assertThat(actualPhrase).isNotBlank();
        assertThat(Character.isUpperCase(actualPhrase.charAt(0))).isTrue();
        assertThat(actualPhrase).doesNotContain("_");
    }

    @Test
    void humanizeIdentifier_givenCamelCase_insertsSpacesBetweenWords() {
        String actualPhrase = textProcessorUnderTest.humanizeIdentifier("helloWorld");
        assertThat(actualPhrase).containsIgnoringCase("hello");
        assertThat(actualPhrase).containsIgnoringCase("world");
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
        "hello world, Hello World",
        "java unit testing, Java Unit Testing"
    })
    void convertToTitleCase_givenLowercaseSentence_returnsCapitalisedWords(
            String plainSentence, String expectedTitleCased) {
        String actualResult = textProcessorUnderTest.convertToTitleCase(plainSentence);
        assertThat(actualResult).isEqualTo(expectedTitleCased);
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
    void truncateToLength_givenShortText_returnsOriginalTextUnchanged() {
        String shortWord = "Hi";
        String actualResult = textProcessorUnderTest.truncateToLength(shortWord, 100);
        assertThat(actualResult).isEqualTo(shortWord);
    }

    @Test
    void truncateToLength_givenTextExactlyAtLimit_returnsTextUnchanged() {
        String exactFitText = "Hello";
        String actualResult = textProcessorUnderTest.truncateToLength(exactFitText, 5);
        assertThat(actualResult).isEqualTo(exactFitText);
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
        "0,    zero",
        "1,    one",
        "42,   forty-two",
        "100,  one hundred",
        "1000, one thousand"
    })
    void convertNumberToWords_givenCommonIntegers_returnsEnglishWords(
            long numericValue, String expectedWords) {
        String actualWords = textProcessorUnderTest.convertNumberToWords(numericValue);
        assertThat(actualWords.trim()).isEqualToIgnoringCase(expectedWords.trim());
    }

    @Test
    void convertNumberToWords_givenNegativeNumber_returnsMinusPrefixedWords() {
        String actualWords = textProcessorUnderTest.convertNumberToWords(-5);
        assertThat(actualWords).containsIgnoringCase("minus");
        assertThat(actualWords).containsIgnoringCase("five");
    }

    @ParameterizedTest(name = "ordinal({0}) => ''{1}''")
    @CsvSource({
        "1,  1st",
        "2,  2nd",
        "3,  3rd",
        "4,  4th",
        "11, 11th",
        "21, 21st"
    })
    void formatAsOrdinal_givenPositiveIntegers_returnsCorrectOrdinalSuffix(
            int positionalNumber, String expectedOrdinal) {
        String actualOrdinal = textProcessorUnderTest.formatAsOrdinal(positionalNumber);
        assertThat(actualOrdinal).isEqualTo(expectedOrdinal.trim());
    }

    @Test
    void formatAsOrdinal_givenNegativeNumber_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> textProcessorUnderTest.formatAsOrdinal(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positionalNumber");
    }

    @ParameterizedTest(name = "byteSize({0}) => ''{1}''")
    @CsvSource({
        "0,       0 B",
        "1024,    1 KB",
        "1048576, 1 MB"
    })
    void formatByteSize_givenCommonSizes_returnsHumanReadableLabel(
            long totalBytes, String expectedLabel) {
        String actualLabel = textProcessorUnderTest.formatByteSize(totalBytes);
        assertThat(actualLabel).isEqualTo(expectedLabel.trim());
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
