package candidate.resource.services.interfaces

import candidate.resource.models.requesters.{CheckVoterStatusRequester, VoteRequester}
import candidate.resource.models.responders.{CheckVoteStatusResponder, StatusResponder}
import candidate.resource.repositories.interfaces.IVoterRepository

import scala.concurrent.Future

trait IVoterService {
  def checkVoteStatus(checkVoterStatusRequester: CheckVoterStatusRequester): Future[CheckVoteStatusResponder]
  def vote(voteRequester: VoteRequester): Future[StatusResponder]
  def voterRepository: IVoterRepository
}
