package skillmanagement.test.docker

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

internal abstract class AbstractDockerContainerExtension<T : Container> : BeforeAllCallback, ParameterResolver {

    private val namespace = Namespace.create(javaClass)

    protected abstract val port: Int
    protected abstract val portProperty: String

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
        extensionContext.container

    override fun beforeAll(context: ExtensionContext) {
        if (context.container == null) {
            val resource = createResource()
                .apply { addExposedPort(port) }
                .apply { start() }
            System.setProperty(portProperty, "${resource.getMappedPort(port)}")
            context.container = resource
        }
    }

    protected abstract fun createResource(): T

    @Suppress("UNCHECKED_CAST")
    private var ExtensionContext.container: T?
        get() = getStore(namespace).get("container") as T?
        set(value) = getStore(namespace).put("container", value)

}
