package candidate.resource.repositories

import candidate.resource.InitializeSpec
import candidate.resource.models.{CandidatesVoted, ElectionResult}
import candidate.resource.repositories.interfaces.IElectionRepository
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS

import java.io.File
import java.sql.{PreparedStatement, ResultSet}

class ElectionRepositorySpec extends  InitializeSpec {

  val mockP: PreparedStatement = Mockito.mock(classOf[PreparedStatement], RETURNS_MOCKS)
  val mockRs: ResultSet = Mockito.mock(classOf[ResultSet], RETURNS_MOCKS)

  test("Get candidates voted success") {
    val id: String = "1"
    val votedCount: Int = 0
    val query: String = s"SELECT id, voted_count from candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true).andThen(false)
    when(mockRs.getString("id")).thenReturn(id)
    when(mockRs.getInt("votedCount")).thenReturn(votedCount)
    val electionRepository: IElectionRepository = new ElectionRepository()
    val result: (List[CandidatesVoted], String) = electionRepository.getCandidatesVoted
    assert(result._1.size == 1)
    assert(result._2.isEmpty)
  }

  test("Get candidates voted failed") {
    val query: String = s"SELECT id, voted_count from candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val electionRepository: IElectionRepository = new ElectionRepository()
    val result: (List[CandidatesVoted], String) = electionRepository.getCandidatesVoted
    assert(result._1.isEmpty)
    assert(result._2 == "failed")
  }

  test("Get election result success") {
    val mockSum = 10
    val sumQuery: String = s"SELECT SUM(voted_count) as total FROM candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(sumQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getInt("total")).thenReturn(mockSum)
    val id: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 10
    val percentage: String = "100%"
    val candidatesQuery: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_count FROM candidates"
    when(postgresWrapper.getConnection.prepareStatement(candidatesQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true).andThen(true).andThen(false)
    when(mockRs.getString("id")).thenReturn(id)
    when(mockRs.getString("name")).thenReturn(name)
    when(mockRs.getString("dob")).thenReturn(dob)
    when(mockRs.getString("bio_link")).thenReturn(bioLink)
    when(mockRs.getString("image_link")).thenReturn(imageLink)
    when(mockRs.getString("policy")).thenReturn(policy)
    when(mockRs.getInt("voted_count")).thenReturn(votedCount)
    val electionRepository: IElectionRepository = new ElectionRepository()
    val result: (List[ElectionResult], String) = electionRepository.getElectionResult
    assert(result._1.size == 1)
    assert(result._1.head.percentage == percentage)
    assert(result._2 == "")
  }

  test("Get election result failed") {
    val mockSum = 10
    val sumQuery: String = s"SELECT SUM(voted_count) as total FROM candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(sumQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getInt("total")).thenReturn(mockSum)
    val candidatesQuery: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_count FROM candidates"
    when(postgresWrapper.getConnection.prepareStatement(candidatesQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val electionRepository: IElectionRepository = new ElectionRepository()
    val result: (List[ElectionResult], String) = electionRepository.getElectionResult
    assert(result._1.isEmpty)
    assert(result._2 == "failed")
  }

  test("Export election result success") {
    val mockCandidateId: String = "1"
    val mockVotedCount: Int = 10
    val query: String = s"SELECT id, voted_count from candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true).andThen(false)
    when(mockRs.getString("id")).thenReturn(mockCandidateId)
    when(mockRs.getInt("voted_count")).thenReturn(mockVotedCount)
    val electionRepository: IElectionRepository = new ElectionRepository()
    val result: File = electionRepository.exportVoteResult
    assert(result != null)
  }

  test("Export election result failed") {
    val query: String = s"SELECT id, voted_count from candidates"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val electionRepository: IElectionRepository = new ElectionRepository()
    val result: File = electionRepository.exportVoteResult
    assert(result == null)
  }

}
