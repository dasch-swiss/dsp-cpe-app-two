package swiss.dasch.cpe

import swiss.dasch.cpe.protocol.GetSuggestionsRequest
import zio._
import swiss.dasch.cpe.protocol.GetSuggestionsResponse

final case class SuggestionsService() {

  private val things = Seq(
    "This",
    "That",
    "maybe this",
    "maybe that",
  )

  def getSuggestions(r: GetSuggestionsRequest): UIO[GetSuggestionsResponse] =
    r match {
      case GetSuggestionsRequest(search, Some(false) | None) =>
        ZIO.succeed(GetSuggestionsResponse(things.filter(_.contains(search))))
      case GetSuggestionsRequest(search, Some(true))         =>
        ZIO.succeed(GetSuggestionsResponse(things.filter(_.startsWith(search))))
    }
}

object SuggestionsService {
  def getSuggestions(r: GetSuggestionsRequest): ZIO[SuggestionsService, Nothing, GetSuggestionsResponse] =
    ZIO.serviceWithZIO[SuggestionsService](_.getSuggestions(r))


  def layer: ZLayer[Any, Nothing, SuggestionsService] =
      ZLayer.succeed(SuggestionsService())
}
