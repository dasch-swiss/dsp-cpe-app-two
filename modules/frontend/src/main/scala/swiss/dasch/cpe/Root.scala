package swiss.dasch.cpe

import com.raquo.laminar.api.L._
import swiss.dasch.cpe.pages.StartPage
import swiss.dasch.cpe.services.SuggestionsService

object Root {

  implicit val runtime: zio.Runtime[Any] = zio.Runtime.default
  val suggestionsService                 = SuggestionsService.live

  def root =
    div(
      StartPage(suggestionsService).page,
    )

}
