package candidate.resource.models.responders

import candidate.resource.models.{Candidate, Error}

final case class CandidatesResponder(results: Option[List[Candidate]], errors: List[Error])
