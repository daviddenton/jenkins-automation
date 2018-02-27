import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.format.Jackson.auto
import org.http4k.format.Xml.xml
import org.w3c.dom.Document


data class PipelineJob(val _class: String?, val description: String?, val displayName: String?, val displayNameOrNull: Any?, val fullDisplayName: String?, val fullName: String?, val name: String?, val url: String?, val buildable: Boolean?, val builds: List<Any>?, val color: String?, val firstBuild: Any?, val healthReport: List<Any>?, val inQueue: Boolean?, val keepDependencies: Boolean?, val lastBuild: Any?, val lastCompletedBuild: Any?, val lastFailedBuild: Any?, val lastStableBuild: Any?, val lastSuccessfulBuild: Any?, val lastUnstableBuild: Any?, val lastUnsuccessfulBuild: Any?, val nextBuildNumber: Number?, val property: List<Any>?, val queueItem: Any?, val concurrentBuild: Boolean?, val resumeBlocked: Boolean?)

class Jenkins(host: String = "ad19cebd91bc211e8903506920e2fbd1-1851510847.eu-west-2.elb.amazonaws.com", token: String) {

    private val client = ClientFilters.SetHostFrom(Uri.of("http://$host:8080"))
            .then(ClientFilters.BasicAuth("admin", token))
            .then(ClientFilters.FollowRedirects())
            .then(DebuggingFilters.PrintRequestAndResponse())
            .then(Filter { next ->
                {
                    next(it.uri(it.uri.port(8080)))
                }
            })
            .then(ApacheClient())

    fun pipelineJob(name: String): PipelineJob {
        val lens = Body.auto<PipelineJob>().toLens()

        val request = Request(GET, "/job/$name/api/json")

        return lens(client(request).apply {
            if (!status.successful) throw RuntimeException(status.toString())
        })
    }

    private val pipelineXmlLens = Body.xml().toLens()

    fun pipelineXml(name: String): Document =
            pipelineXmlLens(client(Request(GET, "/job/$name/config.xml"))
                    .apply {
                        if (!status.successful) throw RuntimeException(status.toString())
                    })

    fun pipelineXml(name: String, doc: Document) {
        client(Request(POST, "/createItem")
                .query("name", name)
                .with(pipelineXmlLens of doc))
                .apply {
                    if (!status.successful) throw RuntimeException(status.toString())
                }
    }
}
