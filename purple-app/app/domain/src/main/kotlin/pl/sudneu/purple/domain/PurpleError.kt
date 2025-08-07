package pl.sudneu.purple.domain

sealed interface PurpleError {
  val message: String

  data class FetchDocumentError(override val message: String) : PurpleError
  data class EmbedDocumentError(override val message: String) : PurpleError
  data class EmbedDocumentQueryError(override val message: String) : PurpleError
  data class RetrieveDocumentsError(override val message: String) : PurpleError
  data class StoreDocumentError(override val message: String) : PurpleError
  data class SearchDocumentError(override val message: String) : PurpleError
  data class UnexpectedError(override val message: String) : PurpleError
  object UnknownError : PurpleError { override val message: String = "Unknown Error" }
}
