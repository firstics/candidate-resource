package candidate.resource.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.models.requesters.{CheckElectionResultRequester, ToggleElectionRequester}
import candidate.resource.services.interfaces.IElectionService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, JsonSupport}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

class ElectionController(implicit val executionContext: ExecutionContextExecutor,
                         implicit val configurationWrapper: IConfigurationWrapper,
                         implicit val electionService: IElectionService,
                         implicit val logger: ILogWrapper) extends Directives with JsonSupport {

  def toggleElection(toggleElectionRequester: ToggleElectionRequester, auth: String): Route = {
    onComplete(electionService.toggleElection(toggleElectionRequester)) {
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
  def checkElectionResult(checkElectionResultRequester: CheckElectionResultRequester, auth: String): Route = {
    onComplete(electionService.checkElectionResult(checkElectionResultRequester)) {
      case Success(value) => {
        val code = if (value.results.get != List.empty) {
          StatusCodes.OK
        }
        else {
          if (value.error.nonEmpty) {
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

  def getElectionResult(auth: String): Route = {
    onComplete(electionService.getElectionResult) {
      case Success(value) => {
        val code = if (value.results != List.empty) {
          StatusCodes.OK
        }
        else {
          if (value.error.nonEmpty) {
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

  def exportCsv(auth: String): Route = {
    onComplete(electionService.exportCsv) {
      case Success(value) => {
        val code = if (value != null) {
          StatusCodes.OK
        }
        else {
          if (value == null) {
            StatusCodes.BadRequest
          } else {
            StatusCodes.InternalServerError
          }
        }
        complete {
          HttpResponse(entity = HttpEntity(ContentTypes.`text/csv(UTF-8)`, write(value)), status = code)
        }
      }
      case Failure(ex) => {
        ex.printStackTrace()
        throw ex
      }
    }
  }
}
