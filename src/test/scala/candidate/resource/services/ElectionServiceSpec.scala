package candidate.resource.services

import candidate.resource.InitializeSpec
import candidate.resource.models.{CandidatesVoted, ElectionResult}
import candidate.resource.models.requesters.{CheckElectionResultRequester, ToggleElectionRequester}
import candidate.resource.repositories.ElectionRepository
import candidate.resource.repositories.interfaces.IElectionRepository
import candidate.resource.services.interfaces.IElectionService
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS

import java.io.File
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

class ElectionServiceSpec extends  InitializeSpec {

  test("Toggle Election") {
    val mockReq: ToggleElectionRequester = ToggleElectionRequester(false)
    val mockRepo: IElectionRepository = Mockito.mock(classOf[ElectionRepository], RETURNS_MOCKS)
    val service: IElectionService = new ElectionService() {
      override def electionRepository: IElectionRepository = mockRepo
    }
    when(configurationWrapper.getElectionStatus).thenReturn(false)
    val futureResult = Await.result(service.toggleElection(mockReq), 5000 millis)
    assert(futureResult.status == "ok")
    assert(!futureResult.enable)
  }

  test("Check election result success") {
    val mockReq: CheckElectionResultRequester = CheckElectionResultRequester(true)
    val cList: List[CandidatesVoted] = List(CandidatesVoted("1", 10), CandidatesVoted("2", 5))
    val mockRepo: IElectionRepository = Mockito.mock(classOf[ElectionRepository], RETURNS_MOCKS)
    val service: IElectionService = new ElectionService() {
      override def electionRepository: IElectionRepository = mockRepo
    }
    when(mockRepo.getCandidatesVoted).thenReturn((cList, ""))
    val futureResult = Await.result(service.checkElectionResult(mockReq), 5000 millis)
    assert(futureResult.results.get.size == 2)
    assert(futureResult.error.head.message.get.isEmpty)
  }

  test("Check election result failed") {
    val mockReq: CheckElectionResultRequester = CheckElectionResultRequester(true)
    val mockRepo: IElectionRepository = Mockito.mock(classOf[ElectionRepository], RETURNS_MOCKS)
    val service: IElectionService = new ElectionService() {
      override def electionRepository: IElectionRepository = mockRepo
    }
    when(mockRepo.getCandidatesVoted).thenReturn((List.empty, "failed"))
    val futureResult = Await.result(service.checkElectionResult(mockReq), 5000 millis)
    assert(futureResult.results.get.isEmpty)
    assert(futureResult.error.head.message.get == "failed")
  }

  test("Get election result success") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 4
    val eList: List[ElectionResult] = List(ElectionResult(mockId, name, dob, bioLink, imageLink, policy, votedCount, "40%"),
      ElectionResult("2", name, dob, bioLink, imageLink, policy, 6, "60%"))
    val mockRepo: IElectionRepository = Mockito.mock(classOf[ElectionRepository], RETURNS_MOCKS)
    val service: IElectionService = new ElectionService() {
      override def electionRepository: IElectionRepository = mockRepo
    }
    when(mockRepo.getElectionResult).thenReturn((eList, ""))
    val futureResult = Await.result(service.getElectionResult, 5000 millis)
    assert(futureResult.results.get.size == 2)
    assert(futureResult.error.head.message.get.isEmpty)
  }

  test("Get election result failed") {
    val mockRepo: IElectionRepository = Mockito.mock(classOf[ElectionRepository], RETURNS_MOCKS)
    val service: IElectionService = new ElectionService() {
      override def electionRepository: IElectionRepository = mockRepo
    }
    when(mockRepo.getElectionResult).thenReturn((List.empty, "failed"))
    val futureResult = Await.result(service.getElectionResult, 5000 millis)
    assert(futureResult.results.get.isEmpty)
    assert(futureResult.error.head.message.get == "failed")
  }

  test("Export CSV success") {
    val mockFile: File = new File("sth.csv")
    val mockRepo: IElectionRepository = Mockito.mock(classOf[ElectionRepository], RETURNS_MOCKS)
    val service: IElectionService = new ElectionService() {
      override def electionRepository: IElectionRepository = mockRepo
    }
    when(mockRepo.exportVoteResult).thenReturn(mockFile)
    val futureResult = Await.result(service.exportCsv, 5000 millis)
    assert(futureResult != null)
  }

  test("Export CSV failed") {
    val mockFile: File = new File("sth.csv")
    val mockRepo: IElectionRepository = Mockito.mock(classOf[ElectionRepository], RETURNS_MOCKS)
    val service: IElectionService = new ElectionService() {
      override def electionRepository: IElectionRepository = mockRepo
    }
    when(mockRepo.exportVoteResult).thenReturn(null)
    val futureResult = Await.result(service.exportCsv, 5000 millis)
    assert(futureResult == null)
  }

}
