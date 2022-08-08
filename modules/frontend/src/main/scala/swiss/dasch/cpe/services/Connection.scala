package swiss.dasch.cpe.services

import zio._
import zio.json._

import com.raquo.airstream.core.EventStream
import swiss.dasch.cpe.domain.DomainError
import io.laminext.fetch.FetchEventStreamBuilder
import io.laminext.fetch.FetchResponse

object Connection {

  def makeRequest[A](
    req: FetchEventStreamBuilder,
  )(implicit jsonDecoderA: JsonDecoder[A],
  ): ZIO[Any, Nothing, EventStream[Either[DomainError, A]]] =
    ZIO
      .attempt {
        req.text
          .map { response =>
            println("FETCH RESPONSE single:" + response)
            handleResponse[A](response)
          }
      }
      .catchAll(catchServiceErrors)

  def handleResponse[A](res: FetchResponse[String])(implicit jsonDecoder: JsonDecoder[A]): Either[DomainError, A] =
    res.status match {
      case 200 =>
        res.data.fromJson[A] match {
          case Right(value) => Right(value)
          case Left(value)  => Left(DomainError.ServiceError.MalformedResult(status = res.status, reason = Some(value)))
        }
    }

  private def catchServiceErrors(err: Throwable) =
    ZIO.succeed(
      EventStream
        .fromValue(
          Left(
            DomainError.ServiceError
              .UnexpectedFailure(status = 500, reason = Some("Unexpected error:" + err.getMessage)),
          ),
        ),
    )

}
