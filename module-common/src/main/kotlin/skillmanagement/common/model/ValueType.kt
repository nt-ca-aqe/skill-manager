package skillmanagement.common.model

import com.fasterxml.jackson.annotation.JsonValue
import skillmanagement.common.validation.Validation
import skillmanagement.common.validation.Validation.Companion.validate

/**
 * A _value type_ (a.k.a. _"domain primitive"_) wraps a given (primitive) value
 * and combines it with requirements that the _value_ has to meet.
 *
 * Other properties of a _value type_ include:
 *
 *  - Equality and hashcode are determined based on the wrapped value.
 *  - String representation (`toString()`) is delegated to the wrapped value.
 *  - Wrapped value is used when serializing instances to JSON.
 */
abstract class ValueType<T : Any>(
    @JsonValue protected val value: T,
    requirements: Validation<T>.() -> Unit = {}
) {

    protected open val label: String = javaClass.simpleName

    init {
        validate(value, label, requirements)
    }

    override fun toString() = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueType<*>

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}
