package pl.sudneu.purple.infrastructure.aws

import dev.forkhandles.result4k.kotest.shouldBeFailure
import dev.forkhandles.result4k.kotest.shouldBeSuccess
import org.http4k.aws.AwsCredentials
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.s3.FakeS3
import org.http4k.connect.amazon.s3.Http
import org.http4k.connect.amazon.s3.S3
import org.http4k.connect.amazon.s3.S3Bucket
import org.http4k.connect.amazon.s3.createBucket
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.amazon.s3.putObject
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.SERVICE_UNAVAILABLE
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.PurpleError.FetchDocumentError
import pl.sudneu.purple.domain.RemoteFileLocation
import java.net.URI

class AwsFetchDocumentShould {

  private val bucketName = BucketName.of("bucket-name")
  private val region = Region.of("eu-central-1")
  private val documentKey = BucketKey.of("${bucketName.value}/document.txt")
  private val credentialsProvider = { AwsCredentials("accesskey", "secret") }
  private val http: HttpHandler = FakeS3()

  private val awsParameters = AwsParameters(
    credentialsProvider,
    bucketName,
    region,
    http,
    null
  )

  @Test
  fun `fetch document from aws`() {
    val documentContent = "Condimentum mi vel primis pretium iaculis."
    val s3 = S3.Http(credentialsProvider, http)
    val bucket = S3Bucket.Http(bucketName, region, credentialsProvider, http)
    s3.createBucket(bucketName, region)
    bucket.putObject(documentKey, documentContent.byteInputStream())
    val fileLocation = RemoteFileLocation(URI.create(documentKey.value))
    val awsFetchDocument = AwsFetchDocument(awsParameters)

    awsFetchDocument(fileLocation) shouldBeSuccess Document(documentContent)
  }

  @Test
  fun `return failure when service is not accessible`() {
    val notAccessibleService: HttpHandler = {
      Response(SERVICE_UNAVAILABLE).body("Server is unavailable")
    }
    val params = awsParameters.copy(client = notAccessibleService)
    val awsFetchDocument = AwsFetchDocument(params)
    val fileLocation = RemoteFileLocation(URI.create(documentKey.value))

    awsFetchDocument(fileLocation) shouldBeFailure FetchDocumentError("Server is unavailable")
  }

  @Test
  fun `return failure when requested document was not found`() {
    val s3 = S3.Http(credentialsProvider, http)
    s3.createBucket(bucketName, region)
    val awsFetchDocument = AwsFetchDocument(awsParameters)
    val fileLocation = RemoteFileLocation(URI.create(documentKey.value))

    awsFetchDocument(fileLocation) shouldBeFailure FetchDocumentError("Document is not found")
  }

  @Test
  fun `return failure when bucket is missed`() {
    val s3 = S3.Http(credentialsProvider, http)
    s3.createBucket(bucketName, region)
    val anotherBucket = BucketName.of("another-bucket")
    val params = awsParameters.copy(bucketName = anotherBucket)
    val awsFetchDocument = AwsFetchDocument(params)
    val fileLocation = RemoteFileLocation(URI.create(documentKey.value))

    awsFetchDocument(fileLocation) shouldBeFailure FetchDocumentError("Document is not found")
  }

  @Test
  fun `return failure when exception is happened`() {
    val client: HttpHandler = { error("unexpected exception") }
    val params = awsParameters.copy(client = client)
    val awsFetchDocument = AwsFetchDocument(params)
    val fileLocation = RemoteFileLocation(URI.create(documentKey.value))

    awsFetchDocument(fileLocation) shouldBeFailure FetchDocumentError("IllegalStateException: unexpected exception")
  }
}
