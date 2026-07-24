package com.example.matchmate.datalayer

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
import kotlinx.coroutines.flow.map

private const val PAGE_SIZE = 10

class MatchRepositoryImpl
@Inject
constructor(private val api: RandomUserApi, private val dao: MatchDao) : MatchRepository {

  override val matches: Flow<List<Match>> = dao.observeAll().map { entities -> entities.map(::toMatch) }

  override suspend fun loadNextPage(): Result<Unit> = tryCatch {
    val nextPage = dao.count() / PAGE_SIZE + 1
    val response = api.getUsers(page = nextPage, results = PAGE_SIZE)
    dao.insertAll(response.results.map { toEntity(it, nextPage) })
  }

  override suspend fun updateDecision(id: String, decision: Decision) {
    dao.updateDecision(id, decision.name)
  }

  private fun toEntity(dto: UserDto, page: Int) =
    MatchEntity(
      id = dto.login.uuid,
      page = page,
      name = "${dto.name.first} ${dto.name.last}",
      age = dto.dob.age,
      location = "${dto.location.city}, ${dto.location.state}",
      pictureUrl = dto.picture.large,
      decision = Decision.NONE.name,
    )

  private fun toMatch(entity: MatchEntity) =
    Match(
      id = entity.id,
      name = entity.name,
      age = entity.age,
      location = entity.location,
      pictureUrl = entity.pictureUrl,
      decision = Decision.valueOf(entity.decision),
    )
}
