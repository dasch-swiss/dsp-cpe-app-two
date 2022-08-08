package swiss.dasch.cpe.protocol

import zio.json._

case class GetSuggestionsRequest(search: String, prefixOnly: Option[Boolean] = None)
object GetSuggestionsRequest {
  implicit val decoder: JsonDecoder[GetSuggestionsRequest] = DeriveJsonDecoder.gen[GetSuggestionsRequest]
  implicit val encoder: JsonEncoder[GetSuggestionsRequest] = DeriveJsonEncoder.gen[GetSuggestionsRequest]
}

case class GetSuggestionsResponse(suggestions: Seq[String])
object GetSuggestionsResponse {
  implicit val decoder: JsonDecoder[GetSuggestionsResponse] = DeriveJsonDecoder.gen[GetSuggestionsResponse]
  implicit val encoder: JsonEncoder[GetSuggestionsResponse] = DeriveJsonEncoder.gen[GetSuggestionsResponse]
}
