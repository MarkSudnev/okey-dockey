package pl.sudneu.purple.domain

interface PurpleError {
  data class FetchDocumentError(val message: String) : PurpleError
  data class EmbedDocumentError(val message: String) : PurpleError
  data class StoreDocumentError(val message: String) : PurpleError
  object UnknownError : PurpleError
}
