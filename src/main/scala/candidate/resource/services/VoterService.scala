package candidate.resource.services

import candidate.resource.models.responders.VoteStatusResponder
import candidate.resource.repositories.VoterRepository
import candidate.resource.repositories.interfaces.IVoterRepository
import candidate.resource.services.interfaces.IVoterService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import scala.concurrent.{ExecutionContextExecutor, Future}

class VoterService(implicit val executionContext: ExecutionContextExecutor,
                   implicit val configurationWrapper: IConfigurationWrapper,
                   implicit val postgresWrapper: IPostgresWrapper,
                   implicit val logger: ILogWrapper) extends IVoterService {
  override def checkVoteStatus(nationalId: String): Future[VoteStatusResponder] = Future {
    val result: (Boolean, String) = voterRepository.getVoter(nationalId)
    VoteStatusResponder(result._1)
  }

  override def voterRepository: IVoterRepository = new VoterRepository
}
