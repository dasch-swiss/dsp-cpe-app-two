package swiss.dasch.cpe.suggestions

import zio._
import zhttp.http._
import swiss.dasch.cpe.protocol.GetSuggestionsRequest
import zio.json._
import swiss.dasch.cpe.SuggestionsService

/**
 * App serving the frontend.
 */
object SuggestionsApp {
  def apply(): Http[SuggestionsService, Nothing, Request, Response] =
    Http.collectZIO[Request] {

      // POST /get-suggestions -d '{"search": "this"}'
      case req @ (Method.POST -> !! / "get-suggestions") =>
        for {
          request  <- req.bodyAsString.map(_.fromJson[GetSuggestionsRequest]).orDie
          response <- request match {
                        case Left(e)  =>
                          ZIO
                            .debug(s"Failed to parse the input: $e")
                            .as(
                              Response.text(e).setStatus(Status.BadRequest),
                            )
                        case Right(r) =>
                          SuggestionsService
                            .getSuggestions(r)
                            .map(_.toJson)
                            .map(Response.json)
                      }
        } yield response
    }
}
