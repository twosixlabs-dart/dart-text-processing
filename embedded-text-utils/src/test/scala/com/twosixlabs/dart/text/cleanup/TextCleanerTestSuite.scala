package com.twosixlabs.dart.text.cleanup

import com.twosixlabs.dart.test.base.StandardTestBase3x

class TextCleanerTestSuite extends StandardTestBase3x {

    "Text Mining Utils" should "normalize text by removing diacritics" in {
        val original = "thís ís á crãzý sëntence"
        val expected = "this is a crazy sentence"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "normalize text by getting rid of extra spaces and new lines" in {
        val original =
            """this
              |
              |is  a    wacky sentence
            """.stripMargin

        val expected = "this is a wacky sentence"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "normalize text by getting rid of anything that is not a letter, as symbols and punctuation are irrelevant" in {
        val original = "this, is a wacky sentence?"

        val expected = "this is a wacky sentence"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "normalize text but leave in some meaningful non-alphanumeric characters" in {
        val original = "this, is á non-wacky sentence?"

        val expected = "this is a non-wacky sentence"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove weight measurements" in {
        val original = "this sentence has 10kgs 10kg 1mg 1gs 1.8g 10lbs 10.12lbs 10lb"

        //        val original = "this sentence has 10g"
        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove height/distance measurements" in {
        val original = "this sentence has 10feet 10foot 10.12feet 10m 10ms 10.12km 1000.1kms 15cm"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove time measurements" in {
        val original = "this sentence has 8minutes 88.888yrs 88seconds 88.88year 88-88hour 8888hr 10years 6-week"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove percent measurements" in {
        val original = "this sentence has 8percent 88.888percent 88percent 88.88percent 88-88percent 8888percent "

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove ordinal measurements" in {
        val original = "this sentence has 1st 2nd 3rd 4th 22.2th 22-23rd"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove powers of ten scalar measurements" in {
        val original = "this sentence has 1million 2.3millions 3hundred 44-45thousand"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove local phone numbers" in {
        val original = "this sentence has 754-3010"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove domestic phone numbers" in {
        val original = "this sentence has (541) 754-3010"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove international phone numbers" in {
        val original = "this sentence has +1-541-754-3010"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "range values with no context" in {
        val original = "this sentence has 1-7 12.8-12.9"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove international phone numbers dialed in the US" in {
        val original = "this sentence has 1-541-754-3010"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

    "Text Mining Utils" should "remove other phone number forms found in the wild" in {
        val original = "this sentence has 1-301-683-3424"

        val expected = "this sentence has"

        val result = TextCleaner.cleanUp( original )
        result shouldBe expected
    }

}