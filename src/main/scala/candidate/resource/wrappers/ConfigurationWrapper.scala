package candidate.resource.wrappers

import candidate.resource.wrappers.interfaces.IConfigurationWrapper
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._

class ConfigurationWrapper extends IConfigurationWrapper {
  private final val config: Config = ConfigFactory.load()

  private lazy val settingConfig: Map[String, String] = {
    val conf = config.getConfig("app.settings")
    conf.root.keySet().asScala.map(key => key -> conf.getString(key)).toMap
  }

  private lazy val dBConfig: Map[String, String] = {
    val conf = config.getConfig("app.database")
    conf.root.keySet().asScala.map(key => key -> conf.getString(key)).toMap
  }

  override def getConfig: Config = {
    config
  }

  override def getSettingConfig(key: String): String = {
    settingConfig.getOrElse(key, "")
  }

  override def getDBConfig(key: String): String = {
    dBConfig.getOrElse(key, "")
  }
}
