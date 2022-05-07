package candidate.resource.repositories.interfaces

trait IVoterRepository {
  def getVoter(nationalId: String): (Boolean, String)
  def vote(nationalId: String, candidateId: String): (String, String)
}
