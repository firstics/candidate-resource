package candidate.resource.wrappers.interfaces

import java.sql.{Connection, PreparedStatement, ResultSet}

trait IPostgresWrapper {
  def executeQuery(preparedStatement: PreparedStatement): (ResultSet, String)
  def getConnection: Connection
}
