package candidate.resource.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.services.interfaces.IVoterService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, JsonSupport}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

class VoterController(implicit val executionContext: ExecutionContextExecutor,
                      implicit val configurationWrapper: IConfigurationWrapper,
                      implicit val voterService: IVoterService,
                      implicit val logger: ILogWrapper) extends Directives with JsonSupport {

  def checkVoteStatus(nationalId: String, auth: String): Route = {
    onComplete(voterService.checkVoteStatus(nationalId)) {
      case Success(value) => {
        val code = if (value.status) {
          StatusCodes.OK
        }
        else {
          if (!value.status) {
            StatusCodes.BadRequest
          } else {
            StatusCodes.InternalServerError
          }
        }
        complete {
          HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, write(value)), status = code)
        }
      }
      case Failure(ex) => {
        ex.printStackTrace()
        throw ex
      }
    }
  }

}
