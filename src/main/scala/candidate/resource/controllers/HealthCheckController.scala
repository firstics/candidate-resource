package candidate.resource.controllers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.services.interfaces.IHealthCheckService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, JsonSupport}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

class HealthCheckController(implicit val executionContext: ExecutionContextExecutor,
                            implicit val configurationWrapper: IConfigurationWrapper,
                            implicit val healthCheckService: IHealthCheckService,
                            implicit val logger: ILogWrapper) extends Directives with JsonSupport {
  def healthCheck(): Route = {
    onComplete(healthCheckService.healthCheck()) {
      case Success(value) => {
        val code = if (value.status != null) {
          StatusCodes.OK
        }
        else {
          StatusCodes.InternalServerError
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
}
