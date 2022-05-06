package candidate.resource.wrappers.interfaces

trait ILogWrapper {
  def info(message: String): Unit
  def error(message: String): Unit
  def warning(message: String): Unit
  def debug(message: String): Unit
}
