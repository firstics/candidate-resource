package candidate.resource.repositories.interfaces

import candidate.resource.models.{CandidatesVoted, ElectionResult}

trait IElectionRepository {
  def getCandidatesVoted: (List[CandidatesVoted], String)
  def getElectionResult: (List[ElectionResult], String)
}
