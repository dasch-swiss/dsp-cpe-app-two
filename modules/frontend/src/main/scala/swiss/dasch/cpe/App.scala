package swiss.dasch.cpe

import com.raquo.laminar.api.L._
import org.scalajs.dom

import Root.root

object App {
  def main(args: Array[String]): Unit = {
    val _ = documentEvents.onDomContentLoaded.foreach { _ =>
      val appContainer = dom.document.querySelector("#app")
      appContainer.innerHTML = ""
      val _            = render(appContainer, root)
    }(unsafeWindowOwner)
  }
}
