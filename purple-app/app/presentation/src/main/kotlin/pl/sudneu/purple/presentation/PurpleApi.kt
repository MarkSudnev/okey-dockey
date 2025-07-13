package pl.sudneu.purple.presentation

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND

fun PurpleApi(): HttpHandler = { Response(NOT_FOUND) }
