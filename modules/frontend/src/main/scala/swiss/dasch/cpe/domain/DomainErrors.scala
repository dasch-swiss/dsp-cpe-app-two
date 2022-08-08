package swiss.dasch.cpe.domain

sealed trait DomainError
object DomainError {
  sealed trait SecurityError extends DomainError {
    def code(): String
  }

  object SecurityError {
    final case class Unauthorized(status: Int = 401, code: String = "unauthorized", reason: Option[String])
        extends SecurityError
    final case class Forbidden(status: Int = 403, code: String = "unauthorized", reason: Option[String])
        extends SecurityError
  }

  sealed trait ServiceError extends DomainError {
    def code(): String
    def reason(): Option[String]
  }

  object ServiceError {
    final case class MalformedResult(status: Int, code: String = "malformed.result", reason: Option[String])
        extends ServiceError
    final case class UnexpectedFailure(status: Int, code: String = "unexpected.error", reason: Option[String])
        extends ServiceError
  }
}
