package candidate.resource.models.responders

import candidate.resource.models.{Error, Voter}

final case class VoterResponder(results: Option[Voter], errors: List[Error])
