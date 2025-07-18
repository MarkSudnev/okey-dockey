package pl.sudneu.purple.presentation

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.config.Environment

typealias MessageHandler = (FileReceivedEvent) -> Result<Unit, String>

fun PurpleMessageHandler(
  environment: Environment
): MessageHandler {

  return fun (event: FileReceivedEvent): Result<Unit, String> {
    println("Purple Message Handler")
    return Success(Unit)
  }
}
