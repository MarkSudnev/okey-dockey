package pl.sudneu.purple.presentation

import dev.forkhandles.result4k.peek
import dev.forkhandles.result4k.recover
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.errors.WakeupException
import pl.sudneu.purple.domain.store.ReceiveDocumentMetadata
import pl.sudneu.purple.domain.toPurpleMessage
import pl.sudneu.purple.logging.ApplicationEvent
import pl.sudneu.purple.logging.ApplicationEventHappened
import pl.sudneu.purple.logging.ApplicationEvents
import pl.sudneu.purple.logging.ApplicationStopped
import pl.sudneu.purple.logging.ErrorHappened
import pl.sudneu.purple.logging.FailureHappened
import java.time.Duration


class PurpleMessageHandler(
  val consumer: Consumer<String, FileReceivedEvent>,
  val receiveDocumentMetadata: ReceiveDocumentMetadata,
  val events: ApplicationEventHappened = ApplicationEvents()
) {

  fun listen(topicName: String) {
    consumer.subscribe(setOf(topicName))
    Runtime.getRuntime().addShutdownHook(Thread {
      stop().also { events(ApplicationStopped) }
    })
    events(MessageHandlerStarted)
    try {
      consume(Duration.ofMillis(100))
    } catch (_: WakeupException) {
    } catch (exception: Exception) {
      events(ErrorHappened(
        exception::class.java,
        exception.toPurpleMessage()
      ))
    } finally {
      consumer.close()
      events(MessageHandlerStopped)
    }
  }

  private fun consume(duration: Duration) {
    while (true) {
      handle(consumer.poll(duration))
    }
  }

  private fun handle(records: ConsumerRecords<String, FileReceivedEvent>) =
    records
      .map { record -> record.value().toDocumentMetadata() }
      .filter { metadata ->
        val filename = metadata.fileLocation.uri.toString().lowercase()
        if (!filename.endsWith("txt")) {
          events(UnsupportedFiletype)
        }
        filename.endsWith("txt")
      }
      .forEach { metadata -> receiveDocumentMetadata(metadata)
        .peek { consumer.commitSync() }
        .recover { error ->
          events(FailureHappened(error::class.java, error.message))
        }
      }

  private fun stop() {
    consumer.wakeup()
  }
}

data object MessageHandlerStarted : ApplicationEvent
data object MessageHandlerStopped : ApplicationEvent
data object UnsupportedFiletype : ApplicationEvent { val message = "Only .txt filetype is supported"}
