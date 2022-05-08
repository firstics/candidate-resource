package candidate.resource

import akka.actor._
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.controllers.{CandidateController, ElectionController, HealthCheckController, VoterController}
import candidate.resource.models.requesters.{CandidateRequester, CheckElectionResultRequester, CheckVoterStatusRequester, ToggleElectionRequester, VoteRequester}
import candidate.resource.services.{CandidateService, ElectionService, HealthCheckService, VoterService}
import candidate.resource.services.interfaces.{ICandidateService, IElectionService, IHealthCheckService, IVoterService}
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

  implicit val electionService: IElectionService = new ElectionService()
  implicit val electionController: ElectionController = new ElectionController()

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
                  val voter: CheckVoterStatusRequester = parse(voterJson).extract[CheckVoterStatusRequester]
                  voterController.checkVoteStatus(voter, authorization)
                }
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        post(pathPrefix("vote") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => entity(as[String]) { voterJson => {
                  val voter: VoteRequester = parse(voterJson).extract[VoteRequester]
                  voterController.vote(voter, authorization)
                }
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        post(pathPrefix("election" / "toggle") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => entity(as[String]) { electionJson => {
                  val election: ToggleElectionRequester = parse(electionJson).extract[ToggleElectionRequester]
                  electionController.toggleElection(election, authorization)
                  }
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        get(pathPrefix("election" / "toggle") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => entity(as[String]) { electionJson => {
                  val checkElectionResultRequester: CheckElectionResultRequester = parse(electionJson).extract[CheckElectionResultRequester]
                  electionController.checkElectionResult(checkElectionResultRequester, authorization)
                }
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        get(pathPrefix("election" / "result") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => {
                  electionController.getElectionResult(authorization)
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        get(pathPrefix("election" / "export") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => {
                  electionController.exportCsv(authorization)
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        })
      )
    }
  }
}
