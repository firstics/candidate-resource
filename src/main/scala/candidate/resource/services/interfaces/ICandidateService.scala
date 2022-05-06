package candidate.resource.services.interfaces

import candidate.resource.models.Error
import candidate.resource.models.requesters.CandidateRequester
import candidate.resource.models.responders._
import candidate.resource.repositories.interfaces.ICandidateRepository

import scala.concurrent.Future

trait ICandidateService {
  def getCandidates: Future[CandidatesResponder]
  def getCandidate(candidateId: String): Future[CandidateResponder]
  def createCandidate(candidateRequester: CandidateRequester): Future[CandidateResponder]
  def updateCandidate(candidateId: String, candidateRequester: CandidateRequester): Future[CandidateResponder]
  def deleteCandidate(candidateId: String): Future[DeleteCandidateResponder]
  def validateCandidate(candidateRequester: CandidateRequester): List[Error]
  def candidateRepository: ICandidateRepository
}
