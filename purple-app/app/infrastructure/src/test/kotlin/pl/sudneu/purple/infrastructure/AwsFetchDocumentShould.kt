package pl.sudneu.purple.infrastructure

import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.http4k.aws.AwsCredentials
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.s3.*
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.NonBlankString
import pl.sudneu.purple.domain.RemoteFileLocation
import java.net.URI

class AwsFetchDocumentShould {

  private val bucketName = BucketName.of("bucket-name")
  private val region = Region.of("eu-central-1")
  private val documentKey = BucketKey.of("${bucketName.value}/document.txt")
  private val credentialsProvider = { AwsCredentials("accesskey", "secret") }
  private val http: HttpHandler = FakeS3()

  @Test
  fun `fetch document from aws`() {
    val documentContent = "Condimentum mi vel primis pretium iaculis."
    val s3 = S3.Http(credentialsProvider, http)
    val bucket = S3Bucket.Http(bucketName, region, credentialsProvider, http)
    s3.createBucket(bucketName, region)
    bucket.putObject(documentKey, documentContent.byteInputStream())
    val fileLocation = RemoteFileLocation(URI.create(documentKey.value))
    val awsFetchDocument =
      AwsFetchDocument(credentialsProvider, bucketName, region, http)

    awsFetchDocument(fileLocation) shouldBeSuccess Document(NonBlankString(documentContent))
  }
}
