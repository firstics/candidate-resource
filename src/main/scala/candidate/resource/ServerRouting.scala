package candidate.resource

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.controllers.{CandidateController, HealthCheckController, VoterController}
import candidate.resource.models.requesters.{CandidateRequester, VoteStatusRequester}
import candidate.resource.services.{CandidateService, HealthCheckService, VoterService}
import candidate.resource.services.interfaces.{ICandidateService, IHealthCheckService, IVoterService}
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper, JsonSupport}

import scala.compat.java8.OptionConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor

class ServerRouting(implicit val configurationWrapper: IConfigurationWrapper,
                    implicit val system: ActorSystem,
                    implicit val executionContextExecutor: ExecutionContextExecutor,
                    implicit val logWrapper: ILogWrapper,
                    implicit val postgresWrapper: IPostgresWrapper) extends Directives with JsonSupport{

  implicit val healthCheckService: IHealthCheckService = new HealthCheckService()
  implicit val healthCheckController: HealthCheckController = new HealthCheckController()

  implicit val candidateService: ICandidateService = new CandidateService()
  implicit val candidateController: CandidateController = new CandidateController()

  implicit val voterService: IVoterService = new VoterService()
  implicit val voterController: VoterController = new VoterController()

  def route: Route = {
    val baseRoute = pathPrefix("api")
    baseRoute {
      concat(
        get(path("healthcheck") {
          withRequestTimeout(120.seconds) {
            healthCheckController.healthCheck()
          }
        }),
        get(path("candidates") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => {
                  candidateController.getCandidates(authorization)
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        get(pathPrefix("candidates" / Segment) {
          (candidateId) => {
            extractRequest {
              requester => {
                requester.getHeader("Authorization").asScala match {
                  case Some(HttpHeader(_, authorization)) => {
                    candidateController.getCandidate(candidateId, authorization)
                  }
                  case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
                }
              }
            }
          }
        }),
        post(pathPrefix("candidates") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => entity(as[String]) { candidateJson => {
                    val candidate: CandidateRequester = parse(candidateJson).extract[CandidateRequester]
                    candidateController.createCandidate(candidate, authorization)
                  }
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        put(pathPrefix("candidates"/ Segment) {
          (candidateId) => {
            extractRequest {
              requester => {
                requester.getHeader("Authorization").asScala match {
                  case Some(HttpHeader(_, authorization)) => entity(as[String]) { candidateJson => {
                    val candidate: CandidateRequester = parse(candidateJson).extract[CandidateRequester]
                    candidateController.updateCandidate(candidateId, candidate, authorization)
                  }
                  }
                  case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
                }
              }
            }
          }
        }),
        delete(pathPrefix("candidates" / Segment) {
          (candidateId) => {
            extractRequest {
              requester => {
                requester.getHeader("Authorization").asScala match {
                  case Some(HttpHeader(_, authorization)) => {
                    candidateController.deleteCandidate(candidateId, authorization)
                  }
                  case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
                }
              }
            }
          }
        }),
        post(pathPrefix("vote" / "status") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => entity(as[String]) { voterJson => {
                  val voter: VoteStatusRequester = parse(voterJson).extract[VoteStatusRequester]
                  voterController.checkVoteStatus(voter.nationalId, authorization)
                }
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
      )
    }
  }
}
