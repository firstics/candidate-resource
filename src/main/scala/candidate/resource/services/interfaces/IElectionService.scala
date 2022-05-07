package candidate.resource.services.interfaces

import candidate.resource.models.requesters.{CheckElectionResultRequester, ToggleElectionRequester}
import candidate.resource.models.responders.{CheckElectionResultResponder, ElectionResultResponder, ToggleElectionResponder}
import candidate.resource.repositories.interfaces.IElectionRepository

import scala.concurrent.Future

trait IElectionService {
  def toggleElection(toggleElectionRequester: ToggleElectionRequester): Future[ToggleElectionResponder]
  def checkElectionResult(checkElectionResultRequester: CheckElectionResultRequester): Future[CheckElectionResultResponder]
  def getElectionResult: Future[ElectionResultResponder]
  def electionRepository: IElectionRepository

}
