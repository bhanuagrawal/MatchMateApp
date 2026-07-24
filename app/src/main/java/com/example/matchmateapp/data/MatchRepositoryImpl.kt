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
private const val FIRST_PAGE = 1

class MatchRepositoryImpl
@Inject
constructor(private val api: RandomUserApi, private val dao: MatchDao) : MatchRepository {

  override val matches: Flow<List<Match>> = dao.observeAll().map { entities -> entities.map(::toMatch) }

  override suspend fun loadNextPage(): Result<Boolean> = tryCatch {
    val nextPage = dao.count() / PAGE_SIZE + 1
    val response = api.getUsers(page = nextPage, results = PAGE_SIZE)
    dao.insertAll(response.results.map { toEntity(it, nextPage) })
    response.results.isEmpty()
  }

  override suspend fun syncCache(): Result<Unit> = tryCatch {
    if (dao.count() == 0) {
      loadNextPage().getOrThrow()
    } else {
      val cachedFirstPageIds = dao.getPage(FIRST_PAGE).map { it.id }.toSet()
      val response = api.getUsers(page = FIRST_PAGE, results = PAGE_SIZE)
      val freshFirstPageIds = response.results.map { it.login.uuid }.toSet()

      // No version/etag from this API to check staleness against, so page 1's
      // content itself is the fingerprint: unchanged -> trust the cache as-is,
      // changed -> the whole page ordering underneath us shifted, so start over.
      if (freshFirstPageIds != cachedFirstPageIds) {
        dao.deleteAll()
        dao.insertAll(response.results.map { toEntity(it, FIRST_PAGE) })
      }
    }
    Unit
  }

  override suspend fun refreshMatches(): Result<Boolean> = tryCatch {
    // Fetch before clearing: if this fails (e.g. no connection), the existing
    // cache must stay intact instead of leaving the user with an empty list.
    val response = api.getUsers(page = FIRST_PAGE, results = PAGE_SIZE)
    dao.deleteAll()
    dao.insertAll(response.results.map { toEntity(it, FIRST_PAGE) })
    response.results.isEmpty()
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
