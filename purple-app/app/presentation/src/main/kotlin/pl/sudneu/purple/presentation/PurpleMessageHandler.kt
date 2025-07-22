package pl.sudneu.purple.presentation

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.errors.WakeupException
import pl.sudneu.purple.domain.ReceiveDocumentMetadata
import java.time.Duration


class PurpleMessageHandler(
  val consumer: Consumer<String, FileReceivedEvent>,
  val receiveDocumentMetadata: ReceiveDocumentMetadata
) {

  fun listen(topicName: String) {
    consumer.subscribe(setOf(topicName))
    try {
      consume(Duration.ofMillis(100))
    }
    catch (_: WakeupException) {}
  }

  private fun consume(duration: Duration) {
    while(true) {
      val records = consumer.poll(duration)
      records.forEach { receiveDocumentMetadata(it.value().toDocumentMetadata()) }
    }
  }
}
