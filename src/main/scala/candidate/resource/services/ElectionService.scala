package candidate.resource.services

import candidate.resource.models.{CandidatesVoted, ElectionResult, Error}
import candidate.resource.models.requesters.{CheckElectionResultRequester, ToggleElectionRequester}
import candidate.resource.models.responders.{CheckElectionResultResponder, ElectionResultResponder, ToggleElectionResponder}
import candidate.resource.repositories.ElectionRepository
import candidate.resource.repositories.interfaces.IElectionRepository
import candidate.resource.services.interfaces.IElectionService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import scala.concurrent.{ExecutionContextExecutor, Future}

class ElectionService(implicit val executionContext: ExecutionContextExecutor,
                      implicit val configurationWrapper: IConfigurationWrapper,
                      implicit val postgresWrapper: IPostgresWrapper,
                      implicit val logger: ILogWrapper) extends IElectionService {

  override def toggleElection(toggleElectionRequester: ToggleElectionRequester): Future[ToggleElectionResponder] = Future {
    configurationWrapper.setElectionStatus(toggleElectionRequester.enable)
    ToggleElectionResponder("ok", configurationWrapper.getElectionStatus)
  }

  override def checkElectionResult(checkElectionResultRequester: CheckElectionResultRequester):
    Future[CheckElectionResultResponder] = Future {
    val result: (List[CandidatesVoted], String) = electionRepository.getCandidatesVoted
    CheckElectionResultResponder(Some(result._1), List(Error(Some(result._2))))
  }

  override def getElectionResult: Future[ElectionResultResponder] = Future {
    val result: (List[ElectionResult], String) = electionRepository.getElectionResult
    ElectionResultResponder(result._1, List(Error(Some(result._2))))
  }

  override def electionRepository: IElectionRepository = new ElectionRepository

}
