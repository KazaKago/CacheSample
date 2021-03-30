package com.kazakago.storeflowable.example.flowable

import com.kazakago.storeflowable.FlowableDataStateManager
import com.kazakago.storeflowable.example.api.GithubApi
import com.kazakago.storeflowable.example.cache.GithubInMemoryCache
import com.kazakago.storeflowable.example.cache.GithubOrgsStateManager
import com.kazakago.storeflowable.example.model.GithubOrg
import com.kazakago.storeflowable.paging.PagingStoreFlowableResponder
import java.time.Duration
import java.time.LocalDateTime

class GithubOrgsResponder : PagingStoreFlowableResponder<Unit, GithubOrg> {

    companion object {
        private val EXPIRED_DURATION = Duration.ofMinutes(1)
        private const val PER_PAGE = 20
    }

    private val githubApi = GithubApi()
    private val githubCache = GithubInMemoryCache

    override val key: Unit = Unit

    override val flowableDataStateManager: FlowableDataStateManager<Unit> = GithubOrgsStateManager

    override suspend fun loadData(): List<GithubOrg>? {
        return githubCache.orgsCache
    }

    override suspend fun saveData(data: List<GithubOrg>?, additionalRequest: Boolean) {
        githubCache.orgsCache = data
        if (!additionalRequest) githubCache.orgsCacheCreatedAt = LocalDateTime.now()
    }

    override suspend fun fetchOrigin(data: List<GithubOrg>?, additionalRequest: Boolean): List<GithubOrg> {
        val since = if (additionalRequest) data?.lastOrNull()?.id else null
        return githubApi.getOrgs(since, PER_PAGE)
    }

    override suspend fun needRefresh(data: List<GithubOrg>): Boolean {
        return githubCache.orgsCacheCreatedAt?.let { createdAt ->
            val expiredAt = createdAt + EXPIRED_DURATION
            expiredAt < LocalDateTime.now()
        } ?: true
    }
}
