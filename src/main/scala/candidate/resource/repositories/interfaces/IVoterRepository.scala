package candidate.resource.repositories.interfaces

import candidate.resource.models.Voter

trait IVoterRepository {
  def getVoter(nationalId: String): (Boolean, String)
  def vote(nationalId: String, candidateId: String): (String, String)
  def insertVoter(nationalId: String): (Voter, String)
}
