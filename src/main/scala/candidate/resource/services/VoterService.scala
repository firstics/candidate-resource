package candidate.resource.services

import candidate.resource.models.{Error, Voter}
import candidate.resource.models.requesters.{CheckVoterStatusRequester, CreateVoterRequester, VoteRequester}
import candidate.resource.models.responders.{CheckVoteStatusResponder, StatusResponder, VoterResponder}
import candidate.resource.repositories.VoterRepository
import candidate.resource.repositories.interfaces.IVoterRepository
import candidate.resource.services.interfaces.IVoterService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import scala.concurrent.{ExecutionContextExecutor, Future}

class VoterService(implicit val executionContext: ExecutionContextExecutor,
                   implicit val configurationWrapper: IConfigurationWrapper,
                   implicit val postgresWrapper: IPostgresWrapper,
                   implicit val logger: ILogWrapper) extends IVoterService {
  override def checkVoteStatus(checkVoterStatusRequester: CheckVoterStatusRequester): Future[CheckVoteStatusResponder] = Future {
    if(checkVoterStatusRequester.nationalId.isEmpty){
      CheckVoteStatusResponder(false)
    }
    else {
      val result: (Boolean, String) = voterRepository.getVoter(checkVoterStatusRequester.nationalId)
      CheckVoteStatusResponder(result._1)
    }
  }

  override def vote(voteRequester: VoteRequester): Future[StatusResponder] = Future {
    if(voteRequester.nationalId.isEmpty || voteRequester.candidateId.isEmpty) {
      StatusResponder("error", Some("Invalid nationalId or candidateId"))
    }
    else if(!configurationWrapper.getElectionStatus) {
      StatusResponder("error", Some("Election is closed"))
    }
    else {
      val result: (String, String) = voterRepository.vote(voteRequester.nationalId, voteRequester.candidateId)
      StatusResponder(result._1, Some(result._2))
    }
  }

  override def createVoter(createVoterRequester: CreateVoterRequester): Future[VoterResponder] = Future {
    if(createVoterRequester.nationalId.isEmpty) {
      VoterResponder(Some(null), List(Error(Some("Invalid nationalId"))))
    }
    else {
      val result: (Voter, String) = voterRepository.insertVoter(createVoterRequester.nationalId)
      VoterResponder(Some(result._1), List(Error(Some(result._2))))
    }
  }

  override def voterRepository: IVoterRepository = new VoterRepository

}
