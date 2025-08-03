package pl.sudneu.purple.logging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime


typealias EventWriter = (String) -> Unit
typealias TimeProvider = () -> LocalDateTime
interface ApplicationEvent

data object ApplicationStarted : ApplicationEvent
data object ApplicationStopped : ApplicationEvent

data class ApplicationEventWrapper(
  val event: String,
  val timestamp: LocalDateTime,
  val payload: ApplicationEvent
)

fun interface ApplicationEventHappened {
  operator fun invoke(applicationEvent: ApplicationEvent)
}

val purpleObjectMapper: ObjectMapper = jacksonObjectMapper()
  .registerModule(JavaTimeModule())
  .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

fun ApplicationEvents(
  writer: EventWriter,
  objectMapper: ObjectMapper = purpleObjectMapper,
  timeProvider: TimeProvider = { LocalDateTime.now() }
): ApplicationEventHappened {
  return ApplicationEventHappened { event: ApplicationEvent ->
    val wrappedEvent = ApplicationEventWrapper(
      event = event::class.simpleName ?: "ApplicationEvent",
      timestamp = timeProvider(),
      payload = event
    )
    val serializedEvent = objectMapper.writeValueAsString(wrappedEvent)
    writer(serializedEvent)
  }
}



