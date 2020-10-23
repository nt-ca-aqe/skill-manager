package skillmanagement

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeArchives
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeJars
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.core.importer.ImportOptions
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Component
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.common.stereotypes.EventHandler
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.common.stereotypes.LastingMetric
import skillmanagement.common.stereotypes.Task
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.common.stereotypes.TransientMetric
import java.io.File
import kotlin.reflect.KClass

class BuildDependencyGraph {

    private val options: ImportOptions = ImportOptions()
        .with(DoNotIncludeJars())
        .with(DoNotIncludeArchives())
        .with(DoNotIncludeTests())
    private val classes = ClassFileImporter(options).importPackages("skillmanagement")

    private val businessFunctions = classes.extract(BusinessFunction::class, ClassType.BusinessFunction)
    private val eventHandlers = classes.extract(EventHandler::class, ClassType.EventHandler)
    private val httpAdapters = classes.extract(HttpAdapter::class, ClassType.HttpAdapter)
    private val lastingMetrics = classes.extract(LastingMetric::class, ClassType.LastingMetric)
    private val tasks = classes.extract(Task::class, ClassType.Task)
    private val technicalFunctions = classes.extract(TechnicalFunction::class, ClassType.TechnicalFunction)
    private val transientMetrics = classes.extract(TransientMetric::class, ClassType.TransientMetric)
    private val components = classes.extract(Component::class, ClassType.Component)
        .removeClasseByName("TestDataInserter")
        .removeClasseByName("BusinessFunction")
        .removeClasseByName("EventHandler")
        .removeClasseByName("HttpAdapter")
        .removeClasseByName("LastingMetric")
        .removeClasseByName("Task")
        .removeClasseByName("TechnicalFunction")
        .removeClasseByName("TransientMetric")
    private val allClassesWithTypes = listOf(
        businessFunctions,
        eventHandlers,
        httpAdapters,
        lastingMetrics,
        tasks,
        technicalFunctions,
        transientMetrics,
        components
    ).flatten().sortedBy { it.clazz.name }

    @Test
    fun `generate Neo4J graph data`() {
        createFile("neo4j-graph-data.txt", Neo4JGraphDataGenerator.generate(allClassesWithTypes))
    }

    private fun JavaClasses.extract(annotation: KClass<out Annotation>, type: ClassType) =
        filter { it.isAnnotatedWith(annotation.java) }.map { ClassWithClassType(it, type) }

    private fun List<ClassWithClassType>.removeClasseByName(suffix: String) =
        filter { it.clazz.simpleName != suffix }

    private fun createFile(fileName: String, fileContent: String) {
        val targetFile = File(fileName)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        targetFile.writeText(fileContent)
    }

}

object Neo4JGraphDataGenerator : AbstractGraphDataGenerator() {

    override fun generate(
        classesWithType: List<ClassWithClassType>,
        classesWithDependencies: List<ClassWithDependencies>
    ): String {
        val builder = StringBuilder()
        classesWithType.toCreateNodeStatements().forEach { builder.appendLine(it) }
        classesWithDependencies.toCreateRelationshipsStatements().forEach { builder.appendLine(it) }
        return builder.toString()
    }

    private fun List<ClassWithClassType>.toCreateNodeStatements() =
        map { it.toCreateNodeStatement() }

    private fun ClassWithClassType.toCreateNodeStatement(): String =
        "CREATE (${clazz.simpleName}:$type {name:'${clazz.simpleName}', fullName:'${clazz.name}'})"

    private fun List<ClassWithDependencies>.toCreateRelationshipsStatements() =
        map { it.toCreateRelationshipsStatement() }

    private fun ClassWithDependencies.toCreateRelationshipsStatement(): String {
        val statements = dependencies.map { dependency -> "(${clazz.simpleName})-[:USES]->(${dependency.simpleName})" }
        return statements.joinToString(prefix = "CREATE ", separator = ",\n       ", postfix = "\n")
    }

}

abstract class AbstractGraphDataGenerator {

    fun generate(classesWithType: List<ClassWithClassType>): String {
        val relevantClasses = classesWithType.map { it.clazz }
        val classesWithDependencies = relevantClasses
            .map { clazz -> clazz to getRelevantDependencies(clazz, relevantClasses) }
            .filter { (_, dependencies) -> dependencies.isNotEmpty() }
            .map { (clazz, dependencies) -> ClassWithDependencies(clazz, dependencies) }
        return generate(classesWithType, classesWithDependencies)
    }

    private fun getRelevantDependencies(clazz: JavaClass, relevantClasses: List<JavaClass>) =
        clazz.constructors.map { it.rawParameterTypes }.flatten().filter { relevantClasses.contains(it) }

    protected abstract fun generate(
        classesWithType: List<ClassWithClassType>,
        classesWithDependencies: List<ClassWithDependencies>
    ): String

}

data class ClassWithClassType(val clazz: JavaClass, val type: ClassType)
data class ClassWithDependencies(val clazz: JavaClass, val dependencies: List<JavaClass>)
enum class ClassType {
    BusinessFunction,
    EventHandler,
    HttpAdapter,
    LastingMetric,
    Task,
    TechnicalFunction,
    TransientMetric,
    Component
}
