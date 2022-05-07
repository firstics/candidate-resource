package candidate.resource.repositories

import candidate.resource.models.{CandidatesVoted, ElectionResult}
import candidate.resource.repositories.interfaces.IElectionRepository
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import java.sql.{PreparedStatement, ResultSet}
import scala.concurrent.ExecutionContextExecutor

class ElectionRepository(implicit val configurationWrapper: IConfigurationWrapper,
                         implicit val postgresWrapper: IPostgresWrapper,
                         implicit val logWrapper: ILogWrapper,
                         implicit val executionContext: ExecutionContextExecutor) extends IElectionRepository {

  lazy val CANDIDATE_TABLE: String = configurationWrapper.getDBConfig("candidateTable")

  override def getCandidatesVoted: (List[CandidatesVoted], String) = {
    try {
      var cList: List[CandidatesVoted] = List.empty
      val query: String = s"SELECT id, voted_counted from $CANDIDATE_TABLE"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2.isEmpty) {
        while(returnSet._1.next()) {
          cList = cList :+ CandidatesVoted(returnSet._1.getString("id"),
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
        logWrapper.error(s"[Election Repository] Ex: ${exception.toString}")
        (List.empty, exception.toString)
      }
    }
  }

  override def getElectionResult: (List[ElectionResult], String) = {
    try {
      var eList: List[ElectionResult] = List.empty
      val sumQuery: String = s"SELECT SUM(voted_count) as total FROM $CANDIDATE_TABLE"
      val votePreparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(sumQuery)
      val sumReturnSet: (ResultSet, String) = postgresWrapper.executeQuery(votePreparedStatement)
      if(sumReturnSet._2.isEmpty && sumReturnSet._1.next()){
        val totalVoted: Int = sumReturnSet._1.getInt("total")
        val query: String = s"SELECT id, name, dob, bio_link, image_link, policy, voted_count FROM $CANDIDATE_TABLE"
        val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
        val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
        if(returnSet._2.isEmpty) {
          while(returnSet._1.next()) {
            eList = eList :+ ElectionResult(returnSet._1.getString("id"), returnSet._1.getString("name"),
              returnSet._1.getString("dob"), returnSet._1.getString("bio_link"),
              returnSet._1.getString("image_link"), returnSet._1.getString("policy"),
              returnSet._1.getInt("voted_counted"), (returnSet._1.getInt("voted_counted")/totalVoted * 100).toString + "%")
          }
          (eList, returnSet._2)
        }
        else {
          (List.empty, returnSet._2)
        }
      }
      else {
        (List.empty, sumReturnSet._2)
      }
    }
    catch {
      case exception: Exception => {
        logWrapper.error(s"[Election Repository] Ex: ${exception.toString}")
        (List.empty, exception.toString)
      }
    }
  }
}
