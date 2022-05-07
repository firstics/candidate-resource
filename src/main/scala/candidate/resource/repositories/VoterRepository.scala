package candidate.resource.repositories

import candidate.resource.repositories.interfaces.IVoterRepository
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import java.sql.{PreparedStatement, ResultSet}
import scala.concurrent.ExecutionContextExecutor

class VoterRepository(implicit val configurationWrapper: IConfigurationWrapper,
                      implicit val postgresWrapper: IPostgresWrapper,
                      implicit val logWrapper: ILogWrapper,
                      implicit val executionContext: ExecutionContextExecutor) extends IVoterRepository{
  lazy val VOTER_TABLE: String = configurationWrapper.getDBConfig("voterTable")
  lazy val CANDIDATE_TABLE: String = configurationWrapper.getDBConfig("candidateTable")

  override def getVoter(nationalId: String): (Boolean, String) = {
    try {
      val query: String = s"SELECT * FROM $VOTER_TABLE WHERE id = ?"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      preparedStatement.setString(1, nationalId)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2.isEmpty && returnSet._1.next()) {
        (!returnSet._1.getBoolean("is_voted"), returnSet._2)
      }
      else {
        (false, returnSet._2)
      }
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Voter Repository] Ex: ${exception.toString}")
        (false, exception.toString)
      }
    }
  }

  override def vote(nationalId: String, candidateId: String): (String, String) = {
    try {
      val query: String = s"SELECT is_voted FROM $VOTER_TABLE WHERE id = ?"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      preparedStatement.setString(1, nationalId)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2.isEmpty && returnSet._1.next()) {
        if(returnSet._1.getBoolean("is_voted")) {
          ("error", "Already voted")
        }
        else {
          val candidateQuery: String = s"UPDATE $CANDIDATE_TABLE SET voted_count = voted_count + 1 WHERE id = ? " +
            s"RETURNING id"
          val candidatePreparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(candidateQuery)
          candidatePreparedStatement.setInt(1, candidateId.toInt)
          val candidateReturnSet: (ResultSet, String) = postgresWrapper.executeQuery(candidatePreparedStatement)
          if(candidateReturnSet._2.isEmpty && candidateReturnSet._1.next()) {
            val voterQuery: String = s"UPDATE $VOTER_TABLE SET is_voted = ? WHERE id = ? " +
              s"RETURNING id"
            val voterPreparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(voterQuery)
            voterPreparedStatement.setBoolean(1, true)
            voterPreparedStatement.setString(2, nationalId)
            val voterReturnSet: (ResultSet, String) = postgresWrapper.executeQuery(voterPreparedStatement)
            if(voterReturnSet._2.isEmpty && voterReturnSet._1.next()) {
              ("ok", null)
            }
            else{
              ("error", returnSet._2)
            }
          }
          else {
            ("error", returnSet._2)
          }
        }
      }
      else {
        ("error", returnSet._2)
      }
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Voter Repository] Ex: ${exception.toString}")
        ("error", exception.toString)
      }
    }
  }
}
