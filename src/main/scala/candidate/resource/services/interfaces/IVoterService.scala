package candidate.resource.services.interfaces

import candidate.resource.models.requesters.{CheckVoterStatusRequester, CreateVoterRequester, VoteRequester}
import candidate.resource.models.responders.{CheckVoteStatusResponder, StatusResponder, VoterResponder}
import candidate.resource.repositories.interfaces.IVoterRepository

import scala.concurrent.Future

trait IVoterService {
  def checkVoteStatus(checkVoterStatusRequester: CheckVoterStatusRequester): Future[CheckVoteStatusResponder]
  def vote(voteRequester: VoteRequester): Future[StatusResponder]
  def createVoter(createVoterRequester: CreateVoterRequester): Future[VoterResponder]
  def voterRepository: IVoterRepository
}
