package pl.sudneu.purple.presentation

import dev.forkhandles.result4k.peek
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
    Runtime.getRuntime().addShutdownHook(Thread { stop() })
    try {
      consume(Duration.ofMillis(100))
    }
    catch (_: WakeupException) {}
    finally {
        consumer.close()
    }
  }

  private fun consume(duration: Duration) {
    while(true) {
      val records = consumer.poll(duration)
      records.forEach {
        receiveDocumentMetadata(it.value().toDocumentMetadata()).peek { consumer.commitSync() }
      }
    }
  }

  private fun stop() {
    consumer.wakeup()
  }
}
