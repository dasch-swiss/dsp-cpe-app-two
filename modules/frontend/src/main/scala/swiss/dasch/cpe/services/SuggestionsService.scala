package swiss.dasch.cpe.services

import zio._
import zio.json._

import com.raquo.airstream.core.EventStream
import swiss.dasch.cpe.domain.DomainError
import swiss.dasch.cpe.protocol.GetSuggestionsRequest
import swiss.dasch.cpe.protocol.GetSuggestionsResponse
import swiss.dasch.cpe.services.Connection.makeRequest
import io.laminext.fetch.Fetch

trait SuggestionsService {
  def getSuggestions(
    m: GetSuggestionsRequest,
  ): UIO[EventStream[Either[DomainError, GetSuggestionsResponse]]]
}

object SuggestionsService {
  val live =
    new SuggestionsService {

      override def getSuggestions(
        m: GetSuggestionsRequest,
      ): UIO[EventStream[Either[DomainError, GetSuggestionsResponse]]] =
        makeRequest[GetSuggestionsResponse](
          Fetch.post(
            "http://localhost:8090/get-suggestions",
            body = m.toJson,
          ),
        )
    }
}
