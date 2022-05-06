package candidate.resource.repositories

import candidate.resource.repositories.interfaces.IVoterRepository
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import java.sql.{PreparedStatement, ResultSet}
import scala.concurrent.ExecutionContextExecutor

class VoterRepository(implicit val configurationWrapper: IConfigurationWrapper,
                      implicit val postgresWrapper: IPostgresWrapper,
                      implicit val logWrapper: ILogWrapper,
                      implicit val executionContext: ExecutionContextExecutor) extends IVoterRepository{
  lazy val TABLE_NAME: String = configurationWrapper.getDBConfig("voterTable")

  override def getVoter(nationalId: String): (Boolean, String) = {
    try {
      val query: String = s"SELECT * FROM $TABLE_NAME WHERE id = ?"
      val preparedStatement: PreparedStatement = postgresWrapper.getConnection.prepareStatement(query)
      preparedStatement.setString(1, nationalId)
      val returnSet: (ResultSet, String) = postgresWrapper.executeQuery(preparedStatement)
      if(returnSet._2.isEmpty && returnSet._1.next()) {
        (true, returnSet._2)
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
}
