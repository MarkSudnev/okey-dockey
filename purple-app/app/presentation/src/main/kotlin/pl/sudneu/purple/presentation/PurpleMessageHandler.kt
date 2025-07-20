package pl.sudneu.purple.presentation

import org.apache.kafka.clients.consumer.Consumer
import pl.sudneu.purple.domain.ReceiveDocumentMetadata


class PurpleMessageHandler(
  val consumer: Consumer<String, FileReceivedEvent>,
  val receiveDocumentMetadata: ReceiveDocumentMetadata
) {

}
