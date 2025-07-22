package pl.sudneu.purple.presentation

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class FileReceivedEventDeserializerShould {

  private val deserializer = FileReceivedEventDeserializer()

  @Test
  fun `return null when message value is null`() {
    deserializer.deserialize("topic", null).shouldBeNull()
  }

  @Test
  fun `deserialize message value`() {
    val message = ClassLoader.getSystemResource("message-example.json").readText().toByteArray()
    val event = deserializer.deserialize("topic", message)

    event.shouldNotBeNull {
      this.Key shouldBe "sweet-bucket/sweet-94c13f58-73cf-4ad4-9afa-3823dcada72f.json"
    }
  }
}
