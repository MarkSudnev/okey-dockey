package pl.sudneu.purple.presentation

import org.http4k.config.Environment

typealias MessageHandler = (String) -> Unit

fun PurpleMessageHandler(
  environment: Environment
): MessageHandler {

  return fun (message: String) {
    println("Purple Message Handler")
  }
}
