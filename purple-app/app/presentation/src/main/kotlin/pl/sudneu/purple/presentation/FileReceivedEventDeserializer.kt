package pl.sudneu.purple.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Deserializer

class FileReceivedEventDeserializer(
  private val objectMapper: ObjectMapper = jacksonObjectMapper()
): Deserializer<FileReceivedEvent> {
  override fun deserialize(topic: String?, data: ByteArray?): FileReceivedEvent? {
    return data?.let { objectMapper.readValue(it, FileReceivedEvent::class.java) }
  }

}
