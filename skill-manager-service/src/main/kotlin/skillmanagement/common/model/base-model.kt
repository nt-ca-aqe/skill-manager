package skillmanagement.common.model

import skillmanagement.common.validation.hasMaxLength
import skillmanagement.common.validation.isNotBlank
import skillmanagement.common.validation.matchesPattern

private val namePattern = Regex("""(?U)[\w][\w -]*""") // unicode word characters

abstract class Name(value: String) :
    StringType(value, { isNotBlank(); hasMaxLength(); matchesPattern(namePattern) })

/**
 * Is limited to 10.000 characters which equates to an average of 1.600 words
 * which is about the ideal length of a blog post according to:
 *  - https://buffer.com/library/the-ideal-length-of-everything-online-according-to-science/
 *  - https://foundant.helpjuice.com/77470-forms/229310-how-many-pages-is-10-000-characters
 */
const val DEFAULT_MAX_TEXT_LENGTH = 10_000
const val DEFAULT_MAX_LABEL_LENGTH = 255

abstract class Text(value: String, maxLength: Int = DEFAULT_MAX_TEXT_LENGTH) :
    StringType(value, { isNotBlank(); hasMaxLength(maxLength) })

abstract class Label(value: String, maxLength: Int = DEFAULT_MAX_LABEL_LENGTH) :
    StringType(value, { isNotBlank(); hasMaxLength(maxLength) })
