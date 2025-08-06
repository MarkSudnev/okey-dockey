package pl.sudneu.purple.presentation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import pl.sudneu.purple.domain.DocumentMetadata
import pl.sudneu.purple.domain.RemoteFileLocation
import java.net.URI

@JsonIgnoreProperties(ignoreUnknown = true)
data class FileReceivedEvent(
  val EventName: String,
  val Key: String,
  val Records: List<FileRecord>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FileRecord(val s3: FileS3)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FileS3(val `object`: FileMetadata)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FileMetadata(
  val key: String,
  val size: Int,
  val contentType: String,
  val sequencer: String
)

fun FileReceivedEvent.toDocumentMetadata(): DocumentMetadata {
  val key = Records.first().s3.`object`.key
  return DocumentMetadata(EventName, RemoteFileLocation(URI(key)))
}
