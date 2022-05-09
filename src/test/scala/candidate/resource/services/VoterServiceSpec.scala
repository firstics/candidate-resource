package candidate.resource.services

import candidate.resource.InitializeSpec
import candidate.resource.models.Voter
import candidate.resource.models.requesters.{CheckVoterStatusRequester, CreateVoterRequester, VoteRequester}
import candidate.resource.repositories.VoterRepository
import candidate.resource.repositories.interfaces.IVoterRepository
import candidate.resource.services.interfaces.IVoterService
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_MOCKS

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

class VoterServiceSpec extends InitializeSpec {

  test("Check vote status success") {
    val mockId: String = "1111111111114"
    val mockReq: CheckVoterStatusRequester = CheckVoterStatusRequester(mockId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(mockRepo.getVoter(mockId)).thenReturn((true, ""))
    val futureResult = Await.result(service.checkVoteStatus(mockReq), 5000 millis)
    assert(futureResult.status)
  }

  test("Check vote status got empty nationalId") {
    val mockId: String = ""
    val mockReq: CheckVoterStatusRequester = CheckVoterStatusRequester(mockId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    val futureResult = Await.result(service.checkVoteStatus(mockReq), 5000 millis)
    assert(!futureResult.status)
  }

  test("Check vote status failed") {
    val mockId: String = "1111111111114"
    val mockReq: CheckVoterStatusRequester = CheckVoterStatusRequester(mockId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(mockRepo.getVoter(mockId)).thenReturn((false, ""))
    val futureResult = Await.result(service.checkVoteStatus(mockReq), 5000 millis)
    assert(!futureResult.status)
  }

  test("Voter vote success") {
    val mockNationalId: String = "1111111111114"
    val mockCandidateId: String = "1"
    val mockReq: VoteRequester = VoteRequester(mockNationalId, mockCandidateId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(configurationWrapper.getElectionStatus).thenReturn(true)
    when(mockRepo.vote(mockNationalId, mockCandidateId)).thenReturn(("ok", null))
    val futureResult = Await.result(service.vote(mockReq), 5000 millis)
    assert(futureResult.status == "ok")
    assert(futureResult.message.get == null)
  }

  test("Voter vote got empty nationalId & candidateId") {
    val mockNationalId: String = ""
    val mockCandidateId: String = ""
    val mockReq: VoteRequester = VoteRequester(mockNationalId, mockCandidateId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(configurationWrapper.getElectionStatus).thenReturn(true)
    val futureResult = Await.result(service.vote(mockReq), 5000 millis)
    assert(futureResult.status == "error")
    assert(futureResult.message.get == "Invalid nationalId or candidateId")
  }

  test("Voter vote when election closed") {
    val mockNationalId: String = "1111111111114"
    val mockCandidateId: String = "1"
    val mockReq: VoteRequester = VoteRequester(mockNationalId, mockCandidateId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(configurationWrapper.getElectionStatus).thenReturn(false)
    val futureResult = Await.result(service.vote(mockReq), 5000 millis)
    assert(futureResult.status == "error")
    assert(futureResult.message.get == "Election is closed")
  }

  test("Voter already vote") {
    val mockNationalId: String = "1111111111114"
    val mockCandidateId: String = "1"
    val mockReq: VoteRequester = VoteRequester(mockNationalId, mockCandidateId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(configurationWrapper.getElectionStatus).thenReturn(true)
    when(mockRepo.vote(mockNationalId, mockCandidateId)).thenReturn(("error", "Already voted"))
    val futureResult = Await.result(service.vote(mockReq), 5000 millis)
    assert(futureResult.status == "error")
    assert(futureResult.message.get == "Already voted")
  }

  test("Create voter success") {
    val mockId: String = "1111111111114"
    val mockReq: CreateVoterRequester = CreateVoterRequester(mockId)
    val voter: Voter = Voter(mockId, false)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(mockRepo.insertVoter(mockId)).thenReturn((voter, ""))
    val futureResult = Await.result(service.createVoter(mockReq), 5000 millis)
    assert(futureResult.results.get != null)
    assert(futureResult.errors.head.message.get.isEmpty)
  }

  test("Create voter got empty nationalId") {
    val mockId: String = ""
    val mockReq: CreateVoterRequester = CreateVoterRequester(mockId)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    val futureResult = Await.result(service.createVoter(mockReq), 5000 millis)
    assert(futureResult.results.get == null)
    assert(futureResult.errors.head.message.get == "Invalid nationalId")
  }

  test("Create voter failed") {
    val mockId: String = "1111111111114"
    val mockReq: CreateVoterRequester = CreateVoterRequester(mockId)
    val voter: Voter = Voter(mockId, false)
    val mockRepo: IVoterRepository = Mockito.mock(classOf[VoterRepository], RETURNS_MOCKS)
    val service: IVoterService = new VoterService() {
      override def voterRepository: IVoterRepository = mockRepo
    }
    when(mockRepo.insertVoter(mockId)).thenReturn((null, "failed"))
    val futureResult = Await.result(service.createVoter(mockReq), 5000 millis)
    assert(futureResult.results.get == null)
    assert(futureResult.errors.head.message.get == "failed")
  }

}
