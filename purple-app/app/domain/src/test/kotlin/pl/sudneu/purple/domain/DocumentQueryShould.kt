package pl.sudneu.purple.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import pl.sudneu.purple.domain.retrieve.DocumentQuery
import pl.sudneu.purple.shared.NonBlankString
import pl.sudneu.purple.shared.PositiveInteger

class DocumentQueryShould {

  @Test
  fun `return value as string`() {
    DocumentQuery(
      NonBlankString("Fusce cras diam neque egestas."),
      PositiveInteger(42)
    ).toString() shouldBe "Fusce cras diam neque egestas."
  }

  @Test
  fun `construct object from string and integer`() {
    val query = DocumentQuery("Ex rutrum lobortis euismod venenatis.", 42)
    query.value.value shouldBe "Ex rutrum lobortis euismod venenatis."
    query.resultsCount.value shouldBe 42
  }


}
