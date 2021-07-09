package com.kazakago.storeflowable.pagination.oneway

import com.kazakago.storeflowable.datastate.FlowableDataStateManager

/**
 * Abstract factory class for [PaginatingStoreFlowable] class.
 *
 * Create a class that implements origin or cache data Input / Output according to this interface.
 *
 * @param KEY Specify the type that is the key to retrieve the data. If there is only one data to handle, specify the [Unit] type.
 * @param DATA Specify the type of data to be handled.
 */
interface PaginatingStoreFlowableFactory<KEY, DATA> {

    /**
     * Key to which data to get.
     *
     * Please implement so that you can pass the key from the outside.
     */
    val key: KEY

    /**
     * Used for data state management.
     *
     * Create a class that inherits [FlowableDataStateManager] and assign it.
     */
    val flowableDataStateManager: FlowableDataStateManager<KEY>

    /**
     * The data loading process from cache.
     *
     * @return The loaded data.
     */
    suspend fun loadDataFromCache(): DATA?

    /**
     * The data saving process to cache.
     *
     * @param newData Data to be saved.
     */
    suspend fun saveDataToCache(newData: DATA?)

    /**
     * TODO
     */
    suspend fun saveAppendingDataToCache(cachedData: DATA?, newData: DATA)

    /**
     * The latest data acquisition process from origin.
     *
     * @return [FetchingResult] class including the acquired data.
     */
    suspend fun fetchDataFromOrigin(): FetchingResult<DATA>

    /**
     * TODO
     */
    suspend fun fetchAppendingDataFromOrigin(cachedData: DATA?): FetchingResult<DATA>

    /**
     * Determine if the cache is valid.
     *
     * @param cachedData Current cache data.
     * @return Returns `true` if the cache is invalid and refresh is needed.
     */
    suspend fun needRefresh(cachedData: DATA): Boolean
}
