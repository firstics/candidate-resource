package candidate.resource

import akka.actor._
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.controllers.{CandidateController, ElectionController, HealthCheckController, VoterController}
import candidate.resource.models.requesters.{CandidateRequester, CheckElectionResultRequester, CheckVoterStatusRequester, CreateVoterRequester, ToggleElectionRequester, VoteRequester}
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
          healthCheckController.healthCheck()
        }),
        get(path("candidates") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => {
                  try {
                    candidateController.getCandidates(authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
                    try {
                      candidateController.getCandidate(candidateId, authorization)
                    }
                    catch {
                      case exception: Exception => {
                        logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                        complete((StatusCodes.BadRequest, exception.toString))
                      }
                    }
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
                  try {
                    val candidate: CandidateRequester = parse(candidateJson).extract[CandidateRequester]
                    candidateController.createCandidate(candidate, authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
                    try {
                      val candidate: CandidateRequester = parse(candidateJson).extract[CandidateRequester]
                      candidateController.updateCandidate(candidateId, candidate, authorization)
                    }
                    catch {
                      case exception: Exception => {
                        logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                        complete((StatusCodes.BadRequest, exception.toString))
                      }
                    }
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
                    try {
                      candidateController.deleteCandidate(candidateId, authorization)
                    }
                    catch {
                      case exception: Exception => {
                        logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                        complete((StatusCodes.BadRequest, exception.toString))
                      }
                    }
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
                  try {
                    val voter: CheckVoterStatusRequester = parse(voterJson).extract[CheckVoterStatusRequester]
                    voterController.checkVoteStatus(voter, authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
                  try {
                    val voter: VoteRequester = parse(voterJson).extract[VoteRequester]
                    voterController.vote(voter, authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
                  try {
                    val election: ToggleElectionRequester = parse(electionJson).extract[ToggleElectionRequester]
                    electionController.toggleElection(election, authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
                  try {
                    val checkElectionResultRequester: CheckElectionResultRequester = parse(electionJson).extract[CheckElectionResultRequester]
                    electionController.checkElectionResult(checkElectionResultRequester, authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
                  try {
                    electionController.getElectionResult(authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
                  try {
                    electionController.exportCsv(authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
                }
                case _ => complete((StatusCodes.Unauthorized, "Authorization Header is missing."))
              }
            }
          }
        }),
        post(pathPrefix("people") {
          extractRequest {
            requester => {
              requester.getHeader("Authorization").asScala match {
                case Some(HttpHeader(_, authorization)) => entity(as[String]) { createVoterJson => {
                  try {
                    val voter: CreateVoterRequester = parse(createVoterJson).extract[CreateVoterRequester]
                    voterController.createVoter(voter, authorization)
                  }
                  catch {
                    case exception: Exception => {
                      logWrapper.error(s"[Routing] Ex: ${exception.toString}")
                      complete((StatusCodes.BadRequest, exception.toString))
                    }
                  }
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
