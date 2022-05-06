package candidate.resource

import java.util.UUID
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import candidate.resource.actors.ServerActor
import candidate.resource.models.ServerRequest
import candidate.resource.wrappers.{ConfigurationWrapper, LogWrapper, PostgresWrapper}
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

object Server extends Directives {
  def main(args: Array[String]): Unit = {
    implicit val configurationWrapper: IConfigurationWrapper = new ConfigurationWrapper()
    lazy val appName: String = configurationWrapper.getSettingConfig("name")
    lazy val version: String =  configurationWrapper.getSettingConfig("version")

    implicit val system: ActorSystem = ActorSystem(appName, configurationWrapper.getConfig)


    val systemActor = system.actorOf(Props[ServerActor], appName)
    systemActor ! ServerRequest("corelationId", UUID.randomUUID().toString)

    implicit val executionContextExecutor: ExecutionContextExecutor = system.dispatcher
    implicit val dBWrapper: IPostgresWrapper = new PostgresWrapper()
    implicit val logWrapper: ILogWrapper = new LogWrapper()

    try {
      val serverRoute = new ServerRouting()
      val serverVersionRoute = serverRoute.route
      val routes: Route = serverVersionRoute
      Http().bindAndHandle(routes, "0.0.0.0", Integer.parseInt(configurationWrapper.getSettingConfig("port")))
      Await.result(system.whenTerminated, Duration.Inf)
    }
    catch {
      case e: Exception =>
        println(e.getMessage)
        println(e.getStackTrace.mkString("Array(", ", ", ")"))
        e.printStackTrace()
    }
  }
}
