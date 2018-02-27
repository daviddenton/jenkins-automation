
import org.http4k.format.Xml.asXmlDocument
import java.io.File

fun main(args: Array<String>) {
    val jenkins = Jenkins(token = System.getenv("TOKEN"))
    println(jenkins.pipelineJob("new-pipeline"))
    println(jenkins.pipelineJob("new-pipeline2"))

    val pipeline = File("pipeline.xml").readText().replace("@repo@", "https://github.com/daviddenton/jenkinsautomation").asXmlDocument()

    jenkins.pipelineXml("new-pipeline5", pipeline)
    println(pipeline)
}