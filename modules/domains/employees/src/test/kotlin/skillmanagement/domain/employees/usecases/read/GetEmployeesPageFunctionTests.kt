package skillmanagement.domain.employees.usecases.read

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import skillmanagement.common.model.pageOf
import skillmanagement.common.searchindices.SearchIndex
import skillmanagement.domain.employees.model.EmployeeEntity
import skillmanagement.domain.employees.model.EmployeeId
import skillmanagement.domain.employees.model.employee_jane_doe
import skillmanagement.domain.employees.model.employee_john_doe
import skillmanagement.domain.employees.model.employee_john_smith
import skillmanagement.domain.employees.model.externalProjectId
import skillmanagement.domain.employees.model.externalSkillId
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.UnitTest

@UnitTest
@ResetMocksAfterEachTest
internal class GetEmployeesPageFunctionTests {

    private val getEmployeesFromDataStore: GetEmployeesFromDataStoreFunction = mockk()
    private val searchIndex: SearchIndex<EmployeeEntity, EmployeeId> = mockk()
    private val findEmployees = GetEmployeesPageFunction(getEmployeesFromDataStore, searchIndex)

    @Test
    fun `AllEmployeesQuery just returns all employees from database`() {
        val query = AllEmployeesQuery()
        val ids = listOf(employee_jane_doe.id, employee_john_smith.id)
        every { searchIndex.findAll(query) } returns pageOf(ids)
        every { getEmployeesFromDataStore(ids) } returns employeeMap(employee_john_smith, employee_jane_doe)

        findEmployees(query) shouldBe pageOf(listOf(employee_jane_doe, employee_john_smith))
    }

    @Test
    fun `EmployeesMatchingQuery gets IDs from search index and then corresponding employees from database`() {
        val query = EmployeesMatchingQuery(queryString = "jane")
        val ids = listOf(employee_jane_doe.id)
        every { searchIndex.query(query) } returns pageOf(ids)
        every { getEmployeesFromDataStore(ids) } returns employeeMap(employee_jane_doe)

        findEmployees(query) shouldBe pageOf(listOf(employee_jane_doe))
    }

    @Test
    fun `EmployeesWhoWorkedOnProject gets IDs from search index and then corresponding employees from database`() {
        val query = EmployeesWhoWorkedOnProject(externalProjectId())
        val ids = listOf(employee_john_doe.id)
        every { searchIndex.query(query) } returns pageOf(ids)
        every { getEmployeesFromDataStore(ids) } returns employeeMap(employee_john_doe)

        findEmployees(query) shouldBe pageOf(listOf(employee_john_doe))
    }

    @Test
    fun `EmployeesWithSkill gets IDs from search index and then corresponding employees from database`() {
        val query = EmployeesWithSkill(externalSkillId())
        val ids = listOf(employee_john_doe.id)
        every { searchIndex.query(query) } returns pageOf(ids)
        every { getEmployeesFromDataStore(ids) } returns employeeMap(employee_john_doe)

        findEmployees(query) shouldBe pageOf(listOf(employee_john_doe))
    }

    private fun employeeMap(vararg employees: EmployeeEntity) = employees.map { it.id to it }.toMap()

}
