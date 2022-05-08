package candidate.resource.repositories

import candidate.resource.models.Candidate
import candidate.resource.InitializeSpec
import candidate.resource.repositories.interfaces.ICandidateRepository

import java.sql.{PreparedStatement, ResultSet}
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS

class CandidateRepositorySpec extends InitializeSpec {

  val mockP: PreparedStatement = Mockito.mock(classOf[PreparedStatement], RETURNS_MOCKS)
  val mockRs: ResultSet = Mockito.mock(classOf[ResultSet], RETURNS_MOCKS)

  test("Get list of candidates success") {
    val id: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 0
    val query: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_count FROM candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true).andThen(false)
    when(mockRs.getString("id")).thenReturn(id)
    when(mockRs.getString("name")).thenReturn(name)
    when(mockRs.getString("dob")).thenReturn(dob)
    when(mockRs.getString("bio_link")).thenReturn(bioLink)
    when(mockRs.getString("image_link")).thenReturn(imageLink)
    when(mockRs.getString("policy")).thenReturn(policy)
    when(mockRs.getInt("voted_count")).thenReturn(votedCount)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (List[Candidate], String) = candidateRepository.getCandidates
    assert(result._1.size == 1)
    assert(result._2.isEmpty)
  }

  test("Get list of candidates failed") {
    val query: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_count FROM candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (List[Candidate], String) = candidateRepository.getCandidates
    assert(result._1.isEmpty)
    assert(result._2 == "failed")
  }

  test("Get candidate success") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 0
    val query: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_count FROM candidates WHERE id = ?"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getString("id")).thenReturn(mockId)
    when(mockRs.getString("name")).thenReturn(name)
    when(mockRs.getString("dob")).thenReturn(dob)
    when(mockRs.getString("bio_link")).thenReturn(bioLink)
    when(mockRs.getString("image_link")).thenReturn(imageLink)
    when(mockRs.getString("policy")).thenReturn(policy)
    when(mockRs.getInt("voted_count")).thenReturn(votedCount)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (Candidate, String) = candidateRepository.getCandidate(mockId)
    assert(result._1 != null)
    assert(result._2.isEmpty)
  }

  test("Get candidate failed") {
    val mockId: String = "1"
    val query: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_count FROM candidates WHERE id = ?"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (Candidate, String) = candidateRepository.getCandidate(mockId)
    assert(result._1 == null)
    assert(result._2 == "failed")
  }

  test("Insert candidate success") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 0
    val query: String = s"INSERT INTO candidates (name, dob, bio_link, image_link, policy) VALUES(?, ?, ?, ?, ?) RETURNING id, voted_count"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getString("id")).thenReturn(mockId)
    when(mockRs.getString("name")).thenReturn(name)
    when(mockRs.getString("dob")).thenReturn(dob)
    when(mockRs.getString("bio_link")).thenReturn(bioLink)
    when(mockRs.getString("image_link")).thenReturn(imageLink)
    when(mockRs.getString("policy")).thenReturn(policy)
    when(mockRs.getInt("voted_count")).thenReturn(votedCount)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (Candidate, String) = candidateRepository.insertCandidate(name, dob, bioLink, imageLink, policy)
    assert(result._1 != null)
    assert(result._2.isEmpty)
  }

  test("Insert candidate failed") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 0
    val query: String = s"INSERT INTO candidates (name, dob, bio_link, image_link, policy) VALUES(?, ?, ?, ?, ?) RETURNING id, voted_count"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (Candidate, String) = candidateRepository.insertCandidate(name, dob, bioLink, imageLink, policy)
    assert(result._1 == null)
    assert(result._2 == "failed")
  }

  test("Update candidate success") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 0
    val query: String = s"UPDATE candidates SET name = ?, dob = ?, bio_link = ?, image_link = ?, policy = ? " +
      s"WHERE id = ? RETURNING id, voted_count"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getString("id")).thenReturn(mockId)
    when(mockRs.getString("name")).thenReturn(name)
    when(mockRs.getString("dob")).thenReturn(dob)
    when(mockRs.getString("bio_link")).thenReturn(bioLink)
    when(mockRs.getString("image_link")).thenReturn(imageLink)
    when(mockRs.getString("policy")).thenReturn(policy)
    when(mockRs.getInt("voted_count")).thenReturn(votedCount)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (Candidate, String) = candidateRepository.updateCandidate(mockId, name, dob, bioLink, imageLink, policy)
    assert(result._1 != null)
    assert(result._2.isEmpty)
  }

  test("Update candidate failed") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val query: String = s"UPDATE candidates SET name = ?, dob = ?, bio_link = ?, image_link = ?, policy = ? " +
      s"WHERE id = ? RETURNING id, voted_count"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (Candidate, String) = candidateRepository.updateCandidate(mockId, name, dob, bioLink, imageLink, policy)
    assert(result._1 == null)
    assert(result._2 == "failed")
  }

  test("Delete candidate success") {
    val mockId: String = "1"
    val query: String = s"DELETE FROM candidates WHERE id = ? RETURNING id"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (String, String) = candidateRepository.deleteCandidate(mockId)
    assert(result._1 == "ok")
    assert(result._2 == null)
  }

  test("Delete candidate failed") {
    val mockId: String = "1"
    val query: String = s"DELETE FROM candidates WHERE id = ? RETURNING id"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val candidateRepository: ICandidateRepository = new CandidateRepository()
    val result: (String, String) = candidateRepository.deleteCandidate(mockId)
    assert(result._1 == "error")
    assert(result._2 == "Candidate not found")
  }
}
