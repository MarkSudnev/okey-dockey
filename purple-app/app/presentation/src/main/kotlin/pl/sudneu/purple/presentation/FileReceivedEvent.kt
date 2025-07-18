package pl.sudneu.purple.presentation

data class FileReceivedEvent(
  val EventName: String,
  val Key: String,
  val Records: List<FileRecord>
)

data class FileRecord(val s3: FileS3)

data class FileS3(val `object`: FileMetadata)

data class FileMetadata(
  val key: String,
  val size: Int,
  val contentType: String,
  val sequencer: String
)
