package candidate.resource.services.interfaces

import candidate.resource.models.responders.VoteStatusResponder
import candidate.resource.repositories.interfaces.IVoterRepository

import scala.concurrent.Future

trait IVoterService {
  def checkVoteStatus(nationalId: String): Future[VoteStatusResponder]
  def voterRepository: IVoterRepository
}
