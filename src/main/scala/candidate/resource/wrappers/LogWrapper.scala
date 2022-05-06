package candidate.resource.wrappers

import akka.actor.ActorSystem
import akka.event.Logging
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper}

class LogWrapper(implicit val actorSystem: ActorSystem,
                 implicit val configurationWrapper: IConfigurationWrapper) extends ILogWrapper {

  private val logger = Logging(actorSystem, configurationWrapper.getSettingConfig("name"))

  override def info(message: String): Unit = {
    println(message)
    logger.info(message)
  }

  override def error(message: String): Unit = {
    println(message)
    logger.error(message)
  }

  override def warning(message: String): Unit = {
    println(message)
    logger.warning(message)
  }

  override def debug(message: String): Unit = {
    println(message)
    logger.debug(message)
  }
}
