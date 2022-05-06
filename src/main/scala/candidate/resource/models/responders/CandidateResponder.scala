package candidate.resource.models.responders

import candidate.resource.models.{Candidate, Error}

final case class CandidateResponder(results: Option[Candidate], errors: List[Error])
