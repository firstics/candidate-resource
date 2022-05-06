package candidate.resource.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.models.requesters.CandidateRequester
import candidate.resource.services.interfaces.ICandidateService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, JsonSupport}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

class CandidateController(implicit val executionContext: ExecutionContextExecutor,
                          implicit val configurationWrapper: IConfigurationWrapper,
                          implicit val candidateService: ICandidateService,
                          implicit val logger: ILogWrapper) extends Directives with JsonSupport {

  def getCandidates(auth: String): Route = {
    onComplete(candidateService.getCandidates) {
      case Success(value) => {
        val code = if (value.results != null) {
          StatusCodes.OK
        }
        else {
          if (value.errors.nonEmpty) {
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

  def getCandidate(candidateId: String, auth: String): Route = {
    onComplete(candidateService.getCandidate(candidateId)) {
      case Success(value) => {
        val code = if (value.results != null) {
          StatusCodes.OK
        }
        else {
          if (value.errors.nonEmpty) {
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

  def createCandidate(candidateRequester: CandidateRequester, auth: String): Route = {
    onComplete(candidateService.createCandidate(candidateRequester)) {
      case Success(value) => {
        val code = if (value.results != null) {
          StatusCodes.OK
        }
        else {
          if (value.errors.nonEmpty) {
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

  def updateCandidate(candidateId: String, candidateRequester: CandidateRequester, auth: String): Route = {
    onComplete(candidateService.updateCandidate(candidateId, candidateRequester)) {
      case Success(value) => {
        val code = if (value.results != null) {
          StatusCodes.OK
        }
        else {
          if (value.errors.nonEmpty) {
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

  def deleteCandidate(candidateId: String, auth: String): Route = {
    onComplete(candidateService.updateCandidate(candidateId, candidateRequester)) {
      case Success(value) => {
        val code = if (value.results != null) {
          StatusCodes.OK
        }
        else {
          if (value.errors.nonEmpty) {
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
