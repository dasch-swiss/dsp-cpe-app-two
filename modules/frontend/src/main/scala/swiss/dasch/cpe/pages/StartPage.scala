package swiss.dasch.cpe.pages

import com.raquo.laminar.api.L._
import swiss.dasch.cpe.protocol.GetSuggestionsRequest
import swiss.dasch.cpe.protocol.GetSuggestionsResponse
import swiss.dasch.cpe.services.SuggestionsService
import io.laminext.syntax.core.thisEvents

final case class StartPage(suggestionsService: SuggestionsService)(implicit val runtime: zio.Runtime[Any]) {

  import StartPage.LoginForm

  private val defaultLoginForm             = LoginForm("", "")
  private val loginFormVar: Var[LoginForm] = Var(defaultLoginForm)

  def page =
    div(
      float("left"),
      width("60%"),
      marginLeft("40%"),
      div(
        float("left"),
        width("100%"),
        div(
          marginTop("15%"),
          width("100%"),
          float("left"),
          div(
            float("left"),
            width("30%"),
            className("form-group"),
            input(
              className("form-control"),
              placeholder("username"),
              value <-- loginFormVar.signal.map(_.username),
              onChange.mapToValue --> { username => loginFormVar.set(loginFormVar.now().copy(username = username)) },
            ),
          ),
        ),
        div(
          float("left"),
          width("100%"),
          marginTop("2%"),
          div(
            float("left"),
            width("30%"),
            className("form-group"),
            input(
              className("form-control"),
              placeholder("password"),
              typ("password"),
              value <-- loginFormVar.signal.map(_.password),
              onChange.mapToValue --> { password => loginFormVar.set(loginFormVar.now().copy(password = password)) },
            ),
          ),
        ),
        div(
          float("left"),
          width("100%"),
          marginTop("4%"),
          div(
            marginLeft("10%"),
            button(
              "Login",
              className := "btn btn-primary",
              thisEvents(onClick)
                .flatMap(_ =>
                  zio.Unsafe
                    .unsafe { implicit u =>
                      runtime.unsafe
                        .run {
                          val loginForm = loginFormVar.now()
                          suggestionsService
                            .getSuggestions(GetSuggestionsRequest("this"))
                        }
                        .getOrThrowFiberFailure()
                    },
                ) --> {
                case Right(GetSuggestionsResponse(suggestions)) =>
                  println(suggestions)
                case _                                          =>
                  println("error case")
              },
            ),
          ),
        ),
      ),
    )
}

object StartPage {
  case class LoginForm(username: String, password: String)

}
