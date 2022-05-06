package candidate.resource.services

import candidate.resource.models.responders.HealthCheckResponder
import candidate.resource.services.interfaces.IHealthCheckService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper}

import scala.concurrent.{ExecutionContextExecutor, Future}

class HealthCheckService(implicit val configurationWrapper: IConfigurationWrapper,
                         implicit val executionContext: ExecutionContextExecutor,
                         implicit val logger: ILogWrapper) extends IHealthCheckService {

  override def healthCheck(): Future[HealthCheckResponder] = Future{
    HealthCheckResponder("ok")
  }
}
