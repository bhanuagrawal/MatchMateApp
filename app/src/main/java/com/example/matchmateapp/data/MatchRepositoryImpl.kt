package com.example.matchmate.datalayer

import com.example.matchmate.datalayer.local.DecisionDao
import com.example.matchmate.datalayer.local.DecisionEntity
import com.example.matchmate.datalayer.local.MatchDao
import com.example.matchmate.datalayer.local.MatchEntity
import com.example.matchmate.datalayer.remote.RandomUserApi
import com.example.matchmate.datalayer.remote.UserDto
import com.example.matchmate.domain.MatchRepository
import com.example.matchmateapp.domain.Decision
import com.example.matchmateapp.domain.Match
import com.example.matchmateapp.tryCatch
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val NETWORK_FETCH_SIZE = 10
private const val FIRST_PAGE = 1

class MatchRepositoryImpl
@Inject
constructor(
  private val api: RandomUserApi,
  private val dao: MatchDao,
  private val decisionDao: DecisionDao,
) : MatchRepository {

  override val matches: Flow<List<Match>> =
    combine(dao.observeAll(), decisionDao.observeAll()) { entities, decisions ->
      val decisionByUserId = decisions.associate { it.userId to Decision.valueOf(it.decision) }
      entities.map { entity -> toMatch(entity, decisionByUserId[entity.id] ?: Decision.NONE) }
    }

  override suspend fun loadNextPage(): Result<Boolean> = tryCatch {
    val startSequence = dao.count()
    val nextPage = startSequence / NETWORK_FETCH_SIZE + 1
    val response = api.getUsers(page = nextPage, results = NETWORK_FETCH_SIZE)
    dao.insertAll(response.results.mapIndexed { index, dto -> toEntity(dto, startSequence + index) })
    response.results.isEmpty()
  }

  override suspend fun syncCache(): Result<Unit> = tryCatch {
    if (dao.count() == 0) {
      loadNextPage().getOrThrow()
    } else {
      val cachedFirstIds = dao.getFirst(NETWORK_FETCH_SIZE).map { it.id }.toSet()
      val response = api.getUsers(page = FIRST_PAGE, results = NETWORK_FETCH_SIZE)
      val freshFirstIds = response.results.map { it.login.uuid }.toSet()

      if (freshFirstIds != cachedFirstIds) {
        dao.deleteAll()
        dao.insertAll(response.results.mapIndexed { index, dto -> toEntity(dto, index) })
      }
    }
    Unit
  }

  override suspend fun refreshMatches(): Result<Boolean> = tryCatch {
    val response = api.getUsers(page = FIRST_PAGE, results = NETWORK_FETCH_SIZE)
    dao.deleteAll()
    dao.insertAll(response.results.mapIndexed { index, dto -> toEntity(dto, index) })
    response.results.isEmpty()
  }

  override suspend fun updateDecision(id: String, decision: Decision) {
    decisionDao.upsert(DecisionEntity(userId = id, decision = decision.name, timestamp = System.currentTimeMillis()))
  }

  private fun toEntity(dto: UserDto, sequence: Int) =
    MatchEntity(
      id = dto.login.uuid,
      sequence = sequence,
      name = "${dto.name.first} ${dto.name.last}",
      age = dto.dob.age,
      location = "${dto.location.city}, ${dto.location.state}",
      pictureUrl = dto.picture.large,
    )

  private fun toMatch(entity: MatchEntity, decision: Decision) =
    Match(
      id = entity.id,
      name = entity.name,
      age = entity.age,
      location = entity.location,
      pictureUrl = entity.pictureUrl,
      decision = decision,
    )
}
