package candidate.resource.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.models.requesters.{CheckVoterStatusRequester, VoteRequester}
import candidate.resource.services.interfaces.IVoterService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, JsonSupport}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

class VoterController(implicit val executionContext: ExecutionContextExecutor,
                      implicit val configurationWrapper: IConfigurationWrapper,
                      implicit val voterService: IVoterService,
                      implicit val logger: ILogWrapper) extends Directives with JsonSupport {

  def checkVoteStatus(checkVoterStatusRequester: CheckVoterStatusRequester, auth: String): Route = {
    onComplete(voterService.checkVoteStatus(checkVoterStatusRequester)) {
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

  def vote(voteRequester: VoteRequester, auth: String): Route = {
    onComplete(voterService.vote(voteRequester)) {
      case Success(value) => {
        val code = if (value.status == "ok") {
          StatusCodes.OK
        }
        else {
          if (value.status == "error") {
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
