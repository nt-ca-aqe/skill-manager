package skillmanagement.common.model

import skillmanagement.common.validation.isBetween

/**
 * [ValueType] for [Int] values.
 *
 * Since integers are already very restrictive by themselves this _value type_
 * only offers the option to set a _min_ and _max_ values. If they are not
 * specified their respective [Int.MIN_VALUE] and [Int.MAX_VALUE] are used.
 *
 * [IntType] also implements [Comparable] based on the natural order of [Int].
 */
abstract class IntType(
    value: Int,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE
) : ValueType<Int>(value, { isBetween(min..max) }), Comparable<IntType> {
    fun toInt() = value
    override fun compareTo(other: IntType) = value.compareTo(other.value)
}
