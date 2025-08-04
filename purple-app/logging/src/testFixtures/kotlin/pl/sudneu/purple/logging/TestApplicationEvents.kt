package pl.sudneu.purple.logging

class TestApplicationEvents(
  val storage: MutableList<ApplicationEvent> = mutableListOf()
): ApplicationEventHappened {

  override fun invoke(applicationEvent: ApplicationEvent) {
    storage.add(applicationEvent)
  }
}
