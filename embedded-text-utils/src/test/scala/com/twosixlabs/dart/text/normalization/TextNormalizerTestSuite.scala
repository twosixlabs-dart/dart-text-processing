package com.twosixlabs.dart.text.normalization

import com.twosixlabs.dart.test.base.StandardTestBase3x

class TextNormalizerTestSuite extends StandardTestBase3x {

    "Embedded Text Normalizer" should "do noything when no-op" in {
        val original = "Hello, my name is Michael"
        val expected = original
        val normalizer = new TextNormalizer()

        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "replace weird dash characters with normal ones" in {
        val original = "Hello, my name is Michael, and this is an annoying dash －"
        val expected = "Hello, my name is Michael, and this is an annoying dash -"
        val normalizer = new TextNormalizer() with Dashes

        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "replace 'smart' quotes" in {
        val original = "Hello, my name is Michael, and “these” are annoying smart quotes"
        val expected = "Hello, my name is Michael, and \"these\" are annoying smart quotes"

        val normalizer = new TextNormalizer() with SmartQuotes
        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "should replace leave weird unicode characters alone" in {
        val original = "﷽"
        val expected = "﷽"

        val normalizer = new TextNormalizer() with SmartQuotes with Dashes with InvisibleFormattingChars
        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "get rid of invisible formatting characters" in {
        val original = "Hello, my name \u001Fis Michael and this has an unprintable unicode character in it"
        val expected = "Hello, my name is Michael and this has an unprintable unicode character in it"


        val normalizer = new TextNormalizer() with InvisibleFormattingChars
        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "leave newlines alone when removing invisible formatting" in {
        val original =
            """Hello, my name \u001Fis Michael and this has an unprintable unicode character in it
              |
              |Here is a new line
            """.stripMargin.trim()

        val expected =
            """Hello, my name is Michael and this has an unprintable unicode character in it
              |
              |Here is a new line
            """.stripMargin.trim()


        val normalizer = new TextNormalizer() with InvisibleFormattingChars
        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "fix weird comma spacing we see a lot in extracted text" in {
        val original = "Hello  , my name is Michael and there is a weird comma in this sentence"
        val expected = "Hello, my name is Michael and there is a weird comma in this sentence"

        val normalizer = new TextNormalizer() with WeirdCommaSpaces
        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "apply all the rules to make wacky text nice" in {
        val original =
            """Hello , my name \u001Fis “Michael” － this is some really crazy text
              |
              |﷽
              |
              |Here is a new line
            """.stripMargin.trim()

        val expected =
            """Hello, my name is "Michael" - this is some really crazy text
              |
              |﷽
              |
              |Here is a new line
            """.stripMargin.trim()

        val normalizer = new TextNormalizer() with InvisibleFormattingChars with WeirdCommaSpaces with SmartQuotes with Dashes
        val result = normalizer.normalize( original )

        result shouldBe expected
    }

    "Embedded Text Normalizer" should "handle all invisible unicode characters" in {
        val original = "Ethio\u00ADpian Pri\u0081me M\u0090inister"
        val expected = "Ethiopian Prime Minister"

        val normalizer = new TextNormalizer() with InvisibleFormattingChars with WeirdCommaSpaces with SmartQuotes with Dashes
        val result = normalizer.normalize( original )

        result shouldBe expected
    }

}