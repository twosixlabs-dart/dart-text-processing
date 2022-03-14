package com.twosixlabs.dart.text.normalization

import scala.collection.mutable.ListBuffer


trait NormalizerOptions {
    protected lazy val REPLACEMENT_RULES : ListBuffer[ (String, String) ] = ListBuffer[ (String, String) ]()
}

trait SmartQuotes extends NormalizerOptions {
    REPLACEMENT_RULES += ( ("[\\u02BA\\u201C\\u201D\\u201E\\u201F\\u2033\\u2036\\u275D\\u275E\\u301D\\u301E\\u301F\\uFF02]", "\"") )
    REPLACEMENT_RULES += ( ("[\\u0060\\u02BB\\u02BC\\u02BD\\u2018\\u2019\\u201A\\u201B\\u275B\\u275C]", "'") )
}

//TODO - there are a lot more dash chars we need to handle
trait Dashes extends NormalizerOptions {
    REPLACEMENT_RULES += (("\\{Dash_Punctuation}", "-"), ("[\\uFF0D\\u2012\\u2013\\u2014\\u2015\\u2053]", "-"))
}

trait InvisibleFormattingChars extends NormalizerOptions {
    REPLACEMENT_RULES += ( ("([\u0000-\u0008]|[\u000B-\u001F])", "") )
    REPLACEMENT_RULES += ( ("[\\p{Zl}][\\p{Zp}]", "\n") )
    REPLACEMENT_RULES += ( ("\\{Cc}", "") )
    REPLACEMENT_RULES += ( ("\\{Cf}", "") )
    REPLACEMENT_RULES += ( ("\u0081", "") )
    REPLACEMENT_RULES += ( ("\u008D", "") )
    REPLACEMENT_RULES += ( ("\u008F", "") )
    REPLACEMENT_RULES += ( ("\u0090", "") )
    REPLACEMENT_RULES += ( ("\u009D", "") )
    REPLACEMENT_RULES += ( ("\u00A0", "") )
    REPLACEMENT_RULES += ( ("\u00AD", "") )
}

trait WeirdCommaSpaces extends NormalizerOptions {
    REPLACEMENT_RULES += ( ("\\p{Space}+,\\p{Space}", ", ") )
}
