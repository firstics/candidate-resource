package candidate.resource.services

import candidate.resource.models.{Candidate, Error}
import candidate.resource.models.requesters.CandidateRequester
import candidate.resource.models.responders.{CandidateResponder, CandidatesResponder, StatusResponder}
import candidate.resource.repositories.CandidateRepository
import candidate.resource.repositories.interfaces.ICandidateRepository
import candidate.resource.services.interfaces.ICandidateService
import candidate.resource.wrappers.interfaces.{IConfigurationWrapper, ILogWrapper, IPostgresWrapper}

import scala.concurrent.{ExecutionContextExecutor, Future}

class CandidateService(implicit val executionContext: ExecutionContextExecutor,
                       implicit val configurationWrapper: IConfigurationWrapper,
                       implicit val postgresWrapper: IPostgresWrapper,
                       implicit val logger: ILogWrapper) extends ICandidateService {
  override def getCandidates: Future[CandidatesResponder] = Future {
    val result: (List[Candidate], String) = candidateRepository.getCandidates
    CandidatesResponder(Some(result._1), List(Error(Some(result._2))))
  }

  override def getCandidate(candidateId: String): Future[CandidateResponder] = Future {
    val result: (Candidate, String) = candidateRepository.getCandidate(candidateId)
    CandidateResponder(Some(result._1), List(Error(Some(result._2))))
  }

  override def createCandidate(candidateRequester: CandidateRequester): Future[CandidateResponder] = Future {
    val errors: List[Error] = validateCandidate(candidateRequester)
    if(errors.isEmpty) {
      val result: (Candidate, String) = candidateRepository.insertCandidate(candidateRequester.name,
        candidateRequester.dob, candidateRequester.bioLink, candidateRequester.imageLink, candidateRequester.policy)
      CandidateResponder(Some(result._1), List(Error(Some(result._2))))
    }
    else {
      CandidateResponder(null, errors)
    }
  }

  override def updateCandidate(candidateId: String, candidateRequester: CandidateRequester): Future[CandidateResponder] = Future {
    val errors: List[Error] = validateCandidate(candidateRequester)
    if(errors.isEmpty) {
      val result: (Candidate, String) = candidateRepository.updateCandidate(candidateId, candidateRequester.name,
        candidateRequester.dob, candidateRequester.bioLink, candidateRequester.imageLink, candidateRequester.policy)
      CandidateResponder(Some(result._1), List(Error(Some(result._2))))
    }
    else {
      CandidateResponder(null, errors)
    }
  }

  override def deleteCandidate(candidateId: String): Future[StatusResponder] = Future {
    val result: (String, String) = candidateRepository.deleteCandidate(candidateId)
    StatusResponder(result._1, Some(result._2))
  }


  override def validateCandidate(candidateRequester: CandidateRequester): List[Error] = {
    var errors: List[Error] = List.empty
    if(candidateRequester.name.isEmpty) {
      errors = errors :+ Error(Some("Name is empty"))
    }
    if(candidateRequester.dob.isEmpty) {
      errors = errors :+ Error(Some("Date of birth is empty"))
    }
    if(candidateRequester.bioLink.isEmpty) {
      errors = errors :+ Error(Some("Bio-link is empty"))
    }
    if(candidateRequester.imageLink.isEmpty) {
      errors = errors :+ Error(Some("Image-link is empty"))
    }
    if(candidateRequester.policy.isEmpty) {
      errors = errors :+ Error(Some("Policy is empty"))
    }
    errors
  }

  override def candidateRepository: ICandidateRepository = new CandidateRepository

}
