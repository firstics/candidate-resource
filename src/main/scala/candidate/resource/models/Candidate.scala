package candidate.resource.models

final case class Candidate
(
  id: String,
  name:String,
  dob: String,
  bioLink: String,
  imageLink: String,
  policy: String,
  votedCount: Int
)
