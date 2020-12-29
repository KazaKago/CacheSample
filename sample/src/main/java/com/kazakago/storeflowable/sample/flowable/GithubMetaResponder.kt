package com.kazakago.storeflowable.sample.flowable

import com.kazakago.storeflowable.FlowableDataStateManager
import com.kazakago.storeflowable.StoreFlowableResponder
import com.kazakago.storeflowable.sample.api.GithubApi
import com.kazakago.storeflowable.sample.cache.GithubInMemoryCache
import com.kazakago.storeflowable.sample.cache.GithubMetaStateManager
import com.kazakago.storeflowable.sample.model.GithubMeta
import java.time.Duration
import java.time.LocalDateTime

class GithubMetaResponder : StoreFlowableResponder<Unit, GithubMeta> {

    companion object {
        private val EXPIRED_DURATION = Duration.ofSeconds(30)
    }

    private val githubApi = GithubApi()
    private val githubCache = GithubInMemoryCache

    override val key: Unit = Unit

    override val flowableDataStateManager: FlowableDataStateManager<Unit> = GithubMetaStateManager

    override suspend fun loadData(): GithubMeta? {
        return githubCache.metaCache
    }

    override suspend fun saveData(data: GithubMeta?) {
        githubCache.metaCache = data
        githubCache.metaCacheCreatedAt = LocalDateTime.now()
    }

    override suspend fun fetchOrigin(): GithubMeta {
        return githubApi.getMeta()
    }

    override suspend fun needRefresh(data: GithubMeta): Boolean {
        return githubCache.metaCacheCreatedAt?.let { createdAt ->
            val expiredAt = createdAt + EXPIRED_DURATION
            expiredAt < LocalDateTime.now()
        } ?: true
    }
}
