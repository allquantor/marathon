package mesosphere.marathon.state

import org.apache.mesos.{ Protos => mesos }
import spray.http.{ StringRendering, Uri }

import scala.collection.immutable.Seq

/**
  * Defaults taken from mesos.proto
  */
case class FetchUri(
    uri: Uri,
    extract: Boolean = true,
    executable: Boolean = false,
    cache: Boolean = false,
    outputFile: Option[String] = None) {

  def toProto: mesos.CommandInfo.URI = {
    Uri.apply()
    val builder = mesos.CommandInfo.URI.newBuilder()
      .setValue(uri.render(new StringRendering).get)
      .setExecutable(executable)
      .setExtract(extract)
      .setCache(cache)
    outputFile.foreach { name => builder.setOutputFile(name) }
    builder.build()
  }
}

object FetchUri {

  val empty: Seq[FetchUri] = Seq.empty

  def fromProto(uri: mesos.CommandInfo.URI): FetchUri =
    FetchUri(
      uri = uri.getValue,
      executable = uri.getExecutable,
      extract = uri.getExtract,
      cache = uri.getCache,
      outputFile = if (uri.hasOutputFile) Some(uri.getOutputFile) else None
    )

  def isExtract(uri: Uri): Boolean = {
    val stringPath = uri.path.render(new StringRendering).get
    stringPath.endsWith(".tgz") ||
      stringPath.endsWith(".tar.gz") ||
      stringPath.endsWith(".tbz2") ||
      stringPath.endsWith(".tar.bz2") ||
      stringPath.endsWith(".txz") ||
      stringPath.endsWith(".tar.xz") ||
      stringPath.endsWith(".zip")
  }
}
