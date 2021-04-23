import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

subprojects {
    apply {
        plugin("io.spring.dependency-management")
    }

    the<DependencyManagementExtension>()
        .apply { imports { mavenBom(SpringBootPlugin.BOM_COORDINATES) } }
}