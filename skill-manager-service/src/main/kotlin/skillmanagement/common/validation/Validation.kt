package skillmanagement.common.validation

import skillmanagement.common.validation.Validation.Companion.validate

/**
 * DSL used to validate generic `value` instances based on a given set of
 * requirements.
 *
 * This is mostly used when defining _value types_ / _domain primitives_.
 * But it can also be used in more complex scenarios with a mix of custom
 * requirements and data classes.
 *
 * @sample integerValueExample
 * @sample customValueExample
 */
class Validation<T>(
    private val label: String,
    val value: T
) {

    private val problems: MutableList<String> = mutableListOf()

    companion object {
        fun <T> validate(value: T, label: String, block: Validation<T>.() -> Unit) {
            Validation(label, value)
                .apply(block)
                .validate()
        }
    }

    fun addViolation(problem: String) {
        problems.add("'$label' [$value] - $problem")
    }

    private fun validate() {
        if (problems.isNotEmpty()) throw ValidationException(label, problems)
    }

}

private fun integerValueExample() {
    validate(42, "Integer") {
        isGreaterThanOrEqualTo(0) // pre-defined reusable function
        if ((value % 2) != 0) { // custom check
            addViolation("must be even")
        }
    }
}

private fun customValueExample() {
    data class Values(val a: Int, val b: Int)

    validate(Values(a = 5, b = 10), "Values") {
        if (((value.a * value.b) % 2) != 0) {
            addViolation("product of a nd b must be even")
        }
    }
}
