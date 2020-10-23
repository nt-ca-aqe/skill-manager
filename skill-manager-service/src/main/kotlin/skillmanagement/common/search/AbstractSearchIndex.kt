package skillmanagement.common.search

import org.elasticsearch.action.DocWriteRequest.OpType.INDEX
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions.DEFAULT
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.unit.TimeValue.timeValueMinutes
import org.elasticsearch.common.xcontent.XContentType.JSON
import org.elasticsearch.index.query.MatchAllQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryStringQueryBuilder
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.ScoreSortBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.sort.SortOrder.DESC
import org.springframework.core.io.Resource
import skillmanagement.common.model.Suggestion
import skillmanagement.common.resources.readAsString
import java.util.UUID
import javax.annotation.PostConstruct

abstract class AbstractSearchIndex<T : Any> {

    protected abstract val client: RestHighLevelClient

    protected abstract val indexName: String
    protected abstract val labelFieldName: String
    protected abstract val sortFieldName: String
    protected abstract val mappingResource: Resource

    @PostConstruct
    fun init() {
        if (!indexExists()) createIndex()
    }

    fun index(instance: T) {
        val request = IndexRequest(indexName)
            .id(id(instance).toString())
            .source(toSource(instance), JSON)
            .opType(INDEX)
        client.index(request, DEFAULT)
    }

    protected abstract fun toSource(instance: T): Map<String, Any>
    protected abstract fun id(instance: T): UUID

    fun deleteById(id: UUID) {
        client.delete(DeleteRequest(indexName, id.toString()), DEFAULT)
    }

    fun query(query: PagedStringQuery): Page<UUID> =
        queryForIds(
            query = buildQuery(query.queryString),
            pageIndex = query.pageIndex.value,
            pageSize = query.pageSize.value,
            sort = ScoreSortBuilder().order(DESC)
        )

    fun findAll(query: PagedFindAllQuery): Page<UUID> =
        queryForIds(
            query = MatchAllQueryBuilder(),
            pageIndex = query.pageIndex.value,
            pageSize = query.pageSize.value,
            sort = FieldSortBuilder(sortFieldName)
        )

    private fun queryForIds(query: QueryBuilder, pageIndex: Int, pageSize: Int, sort: SortBuilder<*>): Page<UUID> {
        val source = SearchSourceBuilder()
            .fetchSource(false)
            .query(query)
            .from(pageIndex * pageSize)
            .size(pageSize)
            .sort(sort)
        val request = SearchRequest(indexName)
            .source(source)

        val response = client.search(request, DEFAULT)

        val searchHits = response.hits
        return Page(
            content = searchHits.hits.map { id(it) },
            pageIndex = pageIndex,
            pageSize = pageSize,
            totalElements = searchHits.totalHits?.value ?: 0
        )
    }

    fun suggestExisting(input: String, size: Int): List<Suggestion> {
        val source = SearchSourceBuilder()
            .fetchSource(labelFieldName, null)
            .query(buildQuery("*$input*"))
            .from(0 * size)
            .size(size)
            .sort(ScoreSortBuilder().order(DESC))
        val request = SearchRequest(indexName)
            .source(source)

        val response = client.search(request, DEFAULT)

        return response.hits.map(::suggestion)
    }

    private fun suggestion(hit: SearchHit): Suggestion =
        Suggestion(
            id = id(hit),
            label = (hit.sourceAsMap[labelFieldName] as? String) ?: ""
        )

    private fun id(it: SearchHit) = UUID.fromString(it.id)

    protected abstract fun buildQuery(queryString: String): QueryStringQueryBuilder

    fun reset() {
        if (indexExists()) {
            deleteIndex()
        }
        createIndex()
    }

    fun refresh() {
        client.indices().refresh(RefreshRequest(indexName), DEFAULT)
    }

    private fun indexExists(): Boolean =
        client.indices().exists(GetIndexRequest(indexName), DEFAULT)

    private fun createIndex() {
        val request = CreateIndexRequest(indexName)
            .mapping(mappingResource.readAsString(), JSON)
        client.indices().create(request, DEFAULT)
    }

    private fun deleteIndex() {
        val request = DeleteIndexRequest(indexName)
            .timeout(timeValueMinutes(1))
        client.indices().delete(request, DEFAULT)
    }

    override fun toString() = "SearchIndex(indexName='$indexName')"

}
