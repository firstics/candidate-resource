package candidate.resource

import akka.actor.ActorSystem
import candidate.resource.wrappers._
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}
import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll, OneInstancePerTest}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContextExecutor

class InitializeSpec extends AsyncFunSuite with Matchers with ScalaFutures with BeforeAndAfterAll with MockitoSugar with OneInstancePerTest {
  implicit val system: ActorSystem = ActorSystem()
  implicit val configurationWrapper: IConfigurationWrapper = Mockito.mock(classOf[ConfigurationWrapper], Mockito.RETURNS_DEEP_STUBS)
  implicit val executionContextExecutor: ExecutionContextExecutor = system.dispatcher
  implicit val postgresWrapper: IPostgresWrapper = Mockito.mock(classOf[PostgresWrapper], Mockito.RETURNS_DEEP_STUBS)
  implicit val logWrapper: ILogWrapper = Mockito.mock(classOf[LogWrapper], Mockito.RETURNS_DEEP_STUBS)
  override protected def afterAll(): Unit = {
    system.terminate()
  }
}
