package candidate.resource.models.responders

import candidate.resource.models.{ElectionResult, Error}

final case class ElectionResultResponder(results: List[ElectionResult], error: List[Error])