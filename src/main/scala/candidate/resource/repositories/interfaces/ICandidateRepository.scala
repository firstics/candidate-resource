package candidate.resource.repositories.interfaces

import candidate.resource.models.Candidate

trait ICandidateRepository {
  def getCandidates: (List[Candidate], String)
  def getCandidate(candidateId: String): (Candidate, String)
  def insertCandidate(name: String, dob: String, bioLink: String, imageLink: String, policy: String): (Candidate, String)
  def updateCandidate(candidateId: String, name: String, dob: String, bioLink: String, imageLink: String, policy: String): (Candidate, String)
  def deleteCandidate(candidateId: String): (String, String)
}
