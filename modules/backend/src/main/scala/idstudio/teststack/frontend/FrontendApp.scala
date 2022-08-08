package swiss.dasch.cpe.frontend

import zhttp.http._
import zio.stream.ZStream

/**
 * App serving the frontend.
 */
object FrontendApp {
  def apply(prod: Boolean): Http[Any, Nothing, Request, Response] =
    Http.collectHttp[Request] {

      // GET /frontend/app.js
      case (Method.GET -> !! / "frontend" / "app.js")                    =>
        if (prod)
          Http.fromStream(ZStream.fromResource("prod.js"))
        else
          Http.fromStream(ZStream.fromResource("dev.js"))

      // GET /frontend
      case Method.GET -> !! / "frontend"                                 =>
        Http.fromStream(ZStream.fromResource("index.html"))

      // GET /assets/ajax-loader.gif
      case Method.GET -> !! / "assets" / path if staticFileAllowed(path) =>
        Http.fromStream(ZStream.fromResource("assets/" + path))
    }

  private def staticFileAllowed(path: String) =
    List(".gif", ".js", ".css", ".map", ".html", ".webm").exists(path.endsWith)
}
