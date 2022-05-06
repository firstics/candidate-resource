package candidate.resource.repositories.interfaces

trait IVoterRepository {
  def getVoter(nationalId: String): (Boolean, String)
}
