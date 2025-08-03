package pl.sudneu.purple.logging

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ApplicationEventsShould {

  @Test
  fun `accept events`() {
    val list = mutableListOf<String>()
    val writer: EventWriter = { event: String -> list.add(event) }
    val events = ApplicationEvents(writer = writer)
    events(ApplicationStarted)
    events(ApplicationStopped)

    list shouldHaveSize 2
  }

  @Test
  fun `print events`() {
    val stringBuilder = StringBuilder()
    val timeProvider = { LocalDateTime.parse("2025-01-01T00:00:00") }
    val writer: EventWriter = { event: String -> stringBuilder.append(event) }
    val events = ApplicationEvents(writer = writer, timeProvider = timeProvider)
    events(TestEvent(message = "Hello World!"))
    stringBuilder.toString() shouldBe """{"event":"TestEvent","timestamp":"2025-01-01T00:00:00","payload":{"message":"Hello World!"}}"""
  }
}

private data class TestEvent(val message: String): ApplicationEvent
