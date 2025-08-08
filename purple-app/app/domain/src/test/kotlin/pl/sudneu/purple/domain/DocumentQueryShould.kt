package pl.sudneu.purple.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import pl.sudneu.purple.shared.NonBlankString

class DocumentQueryShould {

  @Test
  fun `return value as string`() {
    DocumentQuery(
      NonBlankString("Fusce cras diam neque egestas.")
    ).toString() shouldBe "Fusce cras diam neque egestas."
  }

  @Test
  fun `construct object from string`() {
    val query = DocumentQuery(NonBlankString("Ex rutrum lobortis euismod venenatis."))
    query.value.value shouldBe "Ex rutrum lobortis euismod venenatis."
  }
}
