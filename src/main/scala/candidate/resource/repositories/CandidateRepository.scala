package candidate.resource.repositories

import candidate.resource.models.Candidate
import candidate.resource.repositories.interfaces.ICandidateRepository
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import java.sql.{PreparedStatement, ResultSet}
import scala.concurrent.ExecutionContextExecutor

class CandidateRepository(implicit val configurationWrapper: IConfigurationWrapper,
                          implicit val postgresWrapper: IPostgresWrapper,
                          implicit val logWrapper: ILogWrapper,
                          implicit val executionContext: ExecutionContextExecutor) extends ICandidateRepository {

  lazy val CANDIDATE_TABLE: String = configurationWrapper.getDBConfig("candidateTable")

  override def getCandidates: (List[Candidate], String) = {
    try {
      var cList: List[Candidate] = List.empty
      val query: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_counted from $CANDIDATE_TABLE"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2.isEmpty) {
        while(returnSet._1.next()) {
          cList = cList :+ Candidate(returnSet._1.getString("id"), returnSet._1.getString("name"),
            returnSet._1.getString("dob"), returnSet._1.getString("bio_link"),
            returnSet._1.getString("image_link"), returnSet._1.getString("policy"),
            returnSet._1.getInt("voted_counted"))
        }
        (cList, returnSet._2)
      }
      else {
        (List.empty, returnSet._2)
      }
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Candidate Repository] Ex: ${exception.toString}")
        (List.empty, exception.toString)
      }
    }
  }

  override def getCandidate(candidateId: String): (Candidate, String) = {
    try {
      var candidate: Candidate = null
      val query: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_counted FROM $CANDIDATE_TABLE WHERE id = ?"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      preparedStatement.setString(1, candidateId)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2.isEmpty && returnSet._1.next()) {
          candidate = Candidate(returnSet._1.getString("id"), returnSet._1.getString("name"),
            returnSet._1.getString("dob"), returnSet._1.getString("bio_link"),
            returnSet._1.getString("image_link"), returnSet._1.getString("policy"),
            returnSet._1.getInt("voted_counted"))
        (candidate, returnSet._2)
      }
      else {
        (null, returnSet._2)
      }
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Candidate Repository] Ex: ${exception.toString}")
        (null, exception.toString)
      }
    }
  }

  override def insertCandidate(name: String, dob: String, bioLink: String, imageLink: String,
                               policy: String): (Candidate, String) = {
    try {
      val query: String = s"INSERT INTO $CANDIDATE_TABLE (name, dob, bio_link, image_link, policy) VALUES(?, ?, ?, ?, ?) RETURNING id, voted_count"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      preparedStatement.setString(1, name)
      preparedStatement.setString(2, dob)
      preparedStatement.setString(3, bioLink)
      preparedStatement.setString(4, imageLink)
      preparedStatement.setString(5, policy)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2 == "" && returnSet._1.next()) {
        (Candidate(returnSet._1.getString("id"), name, dob, bioLink, imageLink, policy,
          returnSet._1.getInt("voted_count")), returnSet._2)
      }
      else (null, returnSet._2)
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Candidate Repository] Ex: ${exception.toString}")
        (null, exception.toString)
      }
    }
  }

  override def updateCandidate(candidateId: String, name: String, dob: String, bioLink: String, imageLink: String,
                               policy: String): (Candidate, String) = {
    try {
      val query: String = s"UPDATE $CANDIDATE_TABLE SET name = ?, dob = ?, bio_link = ?, image_link = ?, policy = ? " +
        s"WHERE id = ? RETURNING id, voted_count"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      preparedStatement.setString(1, name)
      preparedStatement.setString(2, dob)
      preparedStatement.setString(3, bioLink)
      preparedStatement.setString(4, imageLink)
      preparedStatement.setString(5, policy)
      preparedStatement.setString(6, candidateId)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2 == "" && returnSet._1.next()) {
        (Candidate(returnSet._1.getString("id"), name, dob, bioLink, imageLink, policy,
          returnSet._1.getInt("voted_count")), returnSet._2)
      }
      else (null, returnSet._2)
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Candidate Repository] Ex: ${exception.toString}")
        (null, exception.toString)
      }
    }
  }

  override def deleteCandidate(candidateId: String): (String, String) = {
    try {
      val query: String = s"DELETE FROM $CANDIDATE_TABLE WHERE id = ? RETURNING id"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      preparedStatement.setString(1, candidateId)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2 == "" && returnSet._1.next()) {
        ("ok", null)
      }
      else ("error", returnSet._2)
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Candidate Repository] Ex: ${exception.toString}")
        ("error", exception.toString)
      }
    }
  }
}
