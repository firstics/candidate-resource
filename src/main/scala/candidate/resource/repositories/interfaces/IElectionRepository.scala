package candidate.resource.repositories.interfaces

import candidate.resource.models.{CandidatesVoted, ElectionResult}

import java.io.File

trait IElectionRepository {
  def getCandidatesVoted: (List[CandidatesVoted], String)
  def getElectionResult: (List[ElectionResult], String)
  def exportVoteResult: File
}
