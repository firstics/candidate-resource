package candidate.resource.repositories

import candidate.resource.InitializeSpec
import candidate.resource.repositories.interfaces.IVoterRepository
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS

import java.sql.{PreparedStatement, ResultSet}

class VoterRepositorySpec extends InitializeSpec {

  val mockP: PreparedStatement = Mockito.mock(classOf[PreparedStatement], RETURNS_MOCKS)
  val mockRs: ResultSet = Mockito.mock(classOf[ResultSet], RETURNS_MOCKS)

  test("Get voter success") {
    val mockId: String = "1111111111114"
    val query: String = s"SELECT * FROM voters WHERE id = ?"
    when(configurationWrapper.getDBConfig("voterTable")).thenReturn("voters")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    val voterRepository: IVoterRepository = new VoterRepository()
    val result: (Boolean, String) = voterRepository.getVoter(mockId)
    assert(result._1)
    assert(result._2.isEmpty)
  }

  test("Get voter failed") {
    val mockId: String = "1111111111114"
    val query: String = s"SELECT * FROM voters WHERE id = ?"
    when(configurationWrapper.getDBConfig("voterTable")).thenReturn("voters")
    when(postgresWrapper.getConnection.prepareStatement(query)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val voterRepository: IVoterRepository = new VoterRepository()
    val result: (Boolean, String) = voterRepository.getVoter(mockId)
    assert(!result._1)
    assert(result._2 == "failed")
  }

  test("Voter vote success") {
    val mockNationalId: String = "1111111111114"
    val mockCandidateId: String = "1"
    val chekIsVotedQuery: String = s"SELECT is_voted FROM voters WHERE id = ?"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(configurationWrapper.getDBConfig("voterTable")).thenReturn("voters")
    when(postgresWrapper.getConnection.prepareStatement(chekIsVotedQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getBoolean("is_voted")).thenReturn(false)
    val candidateQuery: String = s"UPDATE candidates SET voted_count = voted_count + 1 WHERE id = ? " +
      s"RETURNING id"
    when(postgresWrapper.getConnection.prepareStatement(candidateQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    val voterQuery: String = s"UPDATE voters SET is_voted = ? WHERE id = ? " +
      s"RETURNING id"
    when(postgresWrapper.getConnection.prepareStatement(voterQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    val voterRepository: IVoterRepository = new VoterRepository()
    val result: (String, String) = voterRepository.vote(mockNationalId, mockCandidateId)
    assert(result._1 == "ok")
    assert(result._2 == null)
  }

  test("Voter already vote") {
    val mockNationalId: String = "1111111111114"
    val mockCandidateId: String = "1"
    val chekIsVotedQuery: String = s"SELECT is_voted FROM voters WHERE id = ?"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(configurationWrapper.getDBConfig("voterTable")).thenReturn("voters")
    when(postgresWrapper.getConnection.prepareStatement(chekIsVotedQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getBoolean("is_voted")).thenReturn(true)
    val voterRepository: IVoterRepository = new VoterRepository()
    val result: (String, String) = voterRepository.vote(mockNationalId, mockCandidateId)
    assert(result._1 == "error")
    assert(result._2 == "Already voted")
  }

  test("Voter vote failed") {
    val mockNationalId: String = "1111111111114"
    val mockCandidateId: String = "1"
    val chekIsVotedQuery: String = s"SELECT is_voted FROM voters WHERE id = ?"
    when(configurationWrapper.getDBConfig("candidateTable")).thenReturn("candidates")
    when(configurationWrapper.getDBConfig("voterTable")).thenReturn("voters")
    when(postgresWrapper.getConnection.prepareStatement(chekIsVotedQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, ""))
    when(mockRs.next()).thenReturn(true)
    when(mockRs.getBoolean("is_voted")).thenReturn(false)
    val candidateQuery: String = s"UPDATE candidates SET voted_count = voted_count + 1 WHERE id = ? " +
      s"RETURNING id"
    when(postgresWrapper.getConnection.prepareStatement(candidateQuery)).thenReturn(mockP)
    when(postgresWrapper.executeQuery(mockP)).thenReturn((mockRs, "failed"))
    when(mockRs.next()).thenReturn(false)
    val voterRepository: IVoterRepository = new VoterRepository()
    val result: (String, String) = voterRepository.vote(mockNationalId, mockCandidateId)
    assert(result._1 == "error")
    assert(result._2 == "failed")
  }



}
