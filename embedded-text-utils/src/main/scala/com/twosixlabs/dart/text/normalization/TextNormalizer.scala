package com.twosixlabs.dart.text.normalization

import java.text.Normalizer

class TextNormalizer extends NormalizerOptions {
    lazy val rules : List[ (String, String) ] = REPLACEMENT_RULES.toList

    //@formatter:off
    def normalize( text : String ) : String = {
        var normalized : String = {
            if ( !Normalizer.isNormalized( text.trim(), java.text.Normalizer.Form.NFKC ) ) Normalizer.normalize( text, java.text.Normalizer.Form.NFKC )
            else text.trim()
        }
        rules.foreach { case (pattern, replacement) => { normalized = normalized.replaceAll( pattern, replacement ) } }
        normalized
    }
}
