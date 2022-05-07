package candidate.resource.models.responders

import candidate.resource.models.{CandidatesVoted, Error}

final case class CheckElectionResultResponder(results: Option[List[CandidatesVoted]], error: List[Error])