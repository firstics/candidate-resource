package candidate.resource.services.interfaces

import candidate.resource.models.responders.HealthCheckResponder

import scala.concurrent.Future

trait IHealthCheckService {
  def healthCheck(): Future[HealthCheckResponder]
}
