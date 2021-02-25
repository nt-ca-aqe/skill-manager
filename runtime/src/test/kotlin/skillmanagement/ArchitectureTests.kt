package skillmanagement

import com.tngtech.archunit.base.DescribedPredicate.alwaysTrue
import com.tngtech.archunit.core.domain.JavaClass.Predicates.type
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeArchives
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeJars
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.core.importer.ImportOptions
import com.tngtech.archunit.library.Architectures
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import skillmanagement.domain.employees.usecases.projectassignments.create.GetProjectByIdAdapter
import skillmanagement.domain.employees.usecases.skillknowledge.set.GetSkillByIdAdapter
import skillmanagement.test.UnitTest

@UnitTest
@TestInstance(PER_CLASS)
internal class ArchitectureTests {

    private val options = ImportOptions()
        .with(DoNotIncludeJars())
        .with(DoNotIncludeArchives())
        .with(DoNotIncludeTests())

    private val basePackage = this::class.java.packageName
    private val domainPackage = "$basePackage.domain"
    private val commonPackage = "$basePackage.common"

    @Test
    fun `no cyclic dependencies between packages`() {
        SlicesRuleDefinition.slices()
            .matching("(**)")
            .should().beFreeOfCycles()
            .check(classesOf(basePackage))
    }

    @Test
    fun `sub domains boundaries are respected`() {
        Architectures.layeredArchitecture()
            .ignoreDependency(type(GetSkillByIdAdapter::class.java), alwaysTrue())
            .ignoreDependency(type(GetProjectByIdAdapter::class.java), alwaysTrue())
            .layer("employees").definedBy("$domainPackage.employees..")
            .layer("projects").definedBy("$domainPackage.projects..")
            .layer("skills").definedBy("$domainPackage.skills..")
            .whereLayer("projects").mayNotBeAccessedByAnyLayer()
            .whereLayer("skills").mayNotBeAccessedByAnyLayer()
            .whereLayer("employees").mayNotBeAccessedByAnyLayer()
            .check(classesOf(domainPackage))
    }

    @Test
    fun `common package does not rely on domain package`() {
        Architectures.layeredArchitecture()
            .layer("common").definedBy("$commonPackage..")
            .layer("domain").definedBy("$domainPackage..")
            .whereLayer("common").mayOnlyBeAccessedByLayers("domain")
            .whereLayer("domain").mayNotBeAccessedByAnyLayer()
            .check(classesOf(commonPackage, domainPackage))
    }

    private fun classesOf(vararg packages: String): JavaClasses =
        ClassFileImporter(options).importPackages(*packages)
}
