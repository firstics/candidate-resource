package candidate.resource.services

import candidate.resource.InitializeSpec
import candidate.resource.models.Candidate
import candidate.resource.models.requesters.CandidateRequester
import candidate.resource.repositories.CandidateRepository
import candidate.resource.repositories.interfaces.ICandidateRepository
import candidate.resource.services.interfaces.ICandidateService
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

class CandidateServiceSpec extends InitializeSpec {

  test("Get list of candidate success") {
    val cList: List[Candidate] = List(Candidate("1", "Brown", "August 8, 2011", "<https://line.fandom.com/wiki/Brown>",
      "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>",
      "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
        "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown",
      10))
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.getCandidates).thenReturn((cList, ""))
    val futureResult = Await.result(service.getCandidates, 5000 millis)
    assert(futureResult.results.get.size == 1)
    assert(futureResult.errors.head.message.get.isEmpty)
  }

  test("Get list of candidate failed") {
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.getCandidates).thenReturn((List.empty, "failed"))
    val futureResult = Await.result(service.getCandidates, 5000 millis)
    assert(futureResult.results.get.isEmpty)
    assert(futureResult.errors.head.message.get == "failed")
  }

  test("Get candidate success") {
    val mockId: String = "1"
    val candidate: Candidate = Candidate("1", "Brown", "August 8, 2011", "<https://line.fandom.com/wiki/Brown>",
      "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>",
      "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
        "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown",
      10)
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.getCandidate(mockId)).thenReturn((candidate, ""))
    val futureResult = Await.result(service.getCandidate(mockId), 5000 millis)
    assert(futureResult.results.get != null)
    assert(futureResult.errors.head.message.get.isEmpty)
  }

  test("Get candidate failed") {
    val mockId: String = "1"
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.getCandidate(mockId)).thenReturn((null, "failed"))
    val futureResult = Await.result(service.getCandidate(mockId), 5000 millis)
    assert(futureResult.results.get == null)
    assert(futureResult.errors.head.message.get == "failed")
  }

  test("Create candidate success") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 0
    val mockCandidate: Candidate = Candidate(mockId, name, dob, bioLink, imageLink, policy, votedCount)
    val mockReq: CandidateRequester = CandidateRequester(name, dob, bioLink, imageLink, policy)
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.insertCandidate(name, dob, bioLink, imageLink, policy)).thenReturn((mockCandidate, ""))
    val futureResult = Await.result(service.createCandidate(mockReq), 5000 millis)
    assert(futureResult.results.get != null)
    assert(futureResult.errors.head.message.get.isEmpty)
  }

  test("Create candidate validate failed") {
    val mockId: String = "1"
    val name: String = ""
    val dob: String = ""
    val bioLink: String = ""
    val imageLink: String = ""
    val policy: String = ""
    val votedCount: Int = 0
    val mockReq: CandidateRequester = CandidateRequester(name, dob, bioLink, imageLink, policy)
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    val futureResult = Await.result(service.createCandidate(mockReq), 5000 millis)
    assert(futureResult.results == null)
    assert(futureResult.errors.size == 5)
  }

  test("Create candidate failed") {
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val mockReq: CandidateRequester = CandidateRequester(name, dob, bioLink, imageLink, policy)
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.insertCandidate(name, dob, bioLink, imageLink, policy)).thenReturn((null, "failed"))
    val futureResult = Await.result(service.createCandidate(mockReq), 5000 millis)
    assert(futureResult.results.get == null)
    assert(futureResult.errors.head.message.get == "failed")
  }

  test("Update candidate success") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val votedCount: Int = 0
    val mockCandidate: Candidate = Candidate(mockId, name, dob, bioLink, imageLink, policy, votedCount)
    val mockReq: CandidateRequester = CandidateRequester(name, dob, bioLink, imageLink, policy)
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.updateCandidate(mockId, name, dob, bioLink, imageLink, policy)).thenReturn((mockCandidate, ""))
    val futureResult = Await.result(service.updateCandidate(mockId, mockReq), 5000 millis)
    assert(futureResult.results.get != null)
    assert(futureResult.errors.head.message.get.isEmpty)
  }

  test("Update candidate validate failed") {
    val mockId: String = "1"
    val name: String = ""
    val dob: String = ""
    val bioLink: String = ""
    val imageLink: String = ""
    val policy: String = ""
    val votedCount: Int = 0
    val mockReq: CandidateRequester = CandidateRequester(name, dob, bioLink, imageLink, policy)
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    val futureResult = Await.result(service.updateCandidate(mockId, mockReq), 5000 millis)
    assert(futureResult.results == null)
    assert(futureResult.errors.size == 5)
  }

  test("Update candidate failed") {
    val mockId: String = "1"
    val name: String = "Brown"
    val dob: String = "August 8, 2011"
    val bioLink: String = "<https://line.fandom.com/wiki/Brown>"
    val imageLink: String = "<https://static.wikia.nocookie.net/line/images/b/bb/2015-brown.png/revision/latest/scale-to-width-down/700?cb=20150808131630>"
    val policy: String = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown"
    val mockReq: CandidateRequester = CandidateRequester(name, dob, bioLink, imageLink, policy)
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.updateCandidate(mockId, name, dob, bioLink, imageLink, policy)).thenReturn((null, "failed"))
    val futureResult = Await.result(service.updateCandidate(mockId, mockReq), 5000 millis)
    assert(futureResult.results.get == null)
    assert(futureResult.errors.head.message.get == "failed")
  }

  test("Delete candidate success") {
    val mockId: String = "1"
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.deleteCandidate(mockId)).thenReturn(("ok", ""))
    val futureResult = Await.result(service.deleteCandidate(mockId), 5000 millis)
    assert(futureResult.status == "ok")
    assert(futureResult.message.get.isEmpty)
  }

  test("Delete candidate failed") {
    val mockId: String = "1"
    val mockRepo: ICandidateRepository = Mockito.mock(classOf[CandidateRepository], RETURNS_MOCKS)
    val service: ICandidateService = new CandidateService() {
      override def candidateRepository: ICandidateRepository = mockRepo
    }
    when(mockRepo.deleteCandidate(mockId)).thenReturn(("error", "Candidate not found"))
    val futureResult = Await.result(service.deleteCandidate(mockId), 5000 millis)
    assert(futureResult.status == "error")
    assert(futureResult.message.get == "Candidate not found")
  }


}
