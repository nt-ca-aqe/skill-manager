package skillmanagement.common.model

import skillmanagement.common.validation.Validation
import skillmanagement.common.validation.hasMaxLength
import skillmanagement.common.validation.isNotBlank

/**
 * [ValueType] for [String] values.
 *
 * Since a _string_ could be basically anything it is highly recommended to
 * define custom requirements to further limit what kinds of values are actually
 * valid.
 *
 * In order to provide a minimum safety net the default requirements are
 * [isNotBlank] and [hasMaxLength]. Making sure that a [StringType] has to have
 * content which is limited with a reasonable default max length.
 *
 * [StringType] also implements [Comparable] based on the natural order of [String].
 */
abstract class StringType(
    value: String,
    requirements: Validation<String>.() -> Unit = { isNotBlank(); hasMaxLength() }
) : ValueType<String>(value, requirements), Comparable<StringType> {
    override fun compareTo(other: StringType) = value.compareTo(other.value)
}
