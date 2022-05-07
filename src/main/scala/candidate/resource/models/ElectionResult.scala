package candidate.resource.models

final case class ElectionResult
(
  id: String,
  name: String,
  dob: String,
  bioLink: String,
  imageLink: String,
  policy: String,
  votedCount: Int,
  percentage: String
)
