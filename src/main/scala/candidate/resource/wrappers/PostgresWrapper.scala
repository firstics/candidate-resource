package candidate.resource.wrappers

import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, IPostgresWrapper}

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import scala.concurrent.ExecutionContextExecutor

class PostgresWrapper(implicit val configurationWrapper: IConfigurationWrapper,
                      implicit val executionContext: ExecutionContextExecutor) extends IPostgresWrapper {

  lazy val conn: Connection = connection(
    configurationWrapper.getDBConfig("host"),
    configurationWrapper.getDBConfig("port").toInt,
    configurationWrapper.getDBConfig("database"),
    configurationWrapper.getDBConfig("user"),
    configurationWrapper.getDBConfig("password")
  )

  def connection(host: String, port: Int, database: String, user: String, password: String): Connection = {
    classOf[org.postgresql.Driver]
    val connection_str = s"jdbc:postgresql://${host}:${port}/${database}?user=${user}&password=${password}"
    DriverManager.getConnection(connection_str)
  }

  override def getConnection: Connection = {
    conn
  }

  override def executeQuery(preparedStatement: PreparedStatement): (ResultSet, String) =  {
    try {
      val result: ResultSet = preparedStatement.executeQuery()
      (result, "")
    }
    catch {
      case ex: Exception => {
        (null, ex.toString)
      }
    }
  }
}
