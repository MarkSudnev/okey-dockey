package pl.sudneu.purple.infrastructure.aws

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.s3.Http
import org.http4k.connect.amazon.s3.S3Bucket
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.HttpHandler
import pl.sudneu.purple.domain.Document
import pl.sudneu.purple.domain.FetchDocument
import pl.sudneu.purple.domain.PurpleError.FetchDocumentError
import pl.sudneu.purple.domain.RemoteFileLocation
import pl.sudneu.purple.domain.handleException

fun AwsFetchDocument(
  credentialsProvider: CredentialsProvider,
  bucketName: BucketName,
  region: Region,
  client: HttpHandler
): FetchDocument = FetchDocument { fileLocation: RemoteFileLocation ->
  handleException {
    val bucket = S3Bucket.Http(bucketName, region, credentialsProvider, client)
    bucket[BucketKey.of(fileLocation.uri.toString())]
      .map { it?.reader()?.readText() }
      .mapFailure { err -> FetchDocumentError(err.message.orEmpty()) }
      .flatMap(String?::toDocument)
  }.mapFailure { err -> FetchDocumentError(err.message) }
}

internal fun String?.toDocument(): Result<Document, FetchDocumentError> =
  this?.let { Document(it).asSuccess() }
  ?: FetchDocumentError("Document is not found").asFailure()

fun FetchDocument.Companion.withAws(
  credentialsProvider: CredentialsProvider,
  bucketName: BucketName,
  region: Region,
  client: HttpHandler
) = AwsFetchDocument(credentialsProvider, bucketName, region, client)
