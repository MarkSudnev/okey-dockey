package pl.sudneu.purple.infrastructure

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
import pl.sudneu.purple.domain.NonBlankString
import pl.sudneu.purple.domain.PurpleError.FetchDocumentError
import pl.sudneu.purple.domain.RemoteFileLocation

fun AwsFetchDocument(
  credentialsProvider: CredentialsProvider,
  bucketName: BucketName,
  region: Region,
  client: HttpHandler
): FetchDocument {
  return FetchDocument { fileLocation: RemoteFileLocation ->
    val bucket = S3Bucket.Http(bucketName, region, credentialsProvider, client)
    bucket[BucketKey.of(fileLocation.uri.toString())]
      .map { it?.reader()?.readText() }
      .mapFailure { err -> FetchDocumentError(err.message.orEmpty()) }
      .flatMap(String?::toDocument)
  }
}

internal fun String?.toDocument(): Result<Document, FetchDocumentError> =
  if (this != null) {
    Document(NonBlankString(this)).asSuccess()
  } else {
    FetchDocumentError("Document is empty").asFailure()
  }

fun FetchDocument.Companion.withAws(
  credentialsProvider: CredentialsProvider,
  bucketName: BucketName,
  region: Region,
  client: HttpHandler
) = AwsFetchDocument(credentialsProvider, bucketName, region, client)
