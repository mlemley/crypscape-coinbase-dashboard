package app.lemley.crypscape.usecase

import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.ui.base.Action
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class SyncProductUseCaseTest {

    private fun createUseCase(
        coinBaseRepository: CoinBaseRepository = mockk(relaxUnitFun = true)
    ): SyncProductUseCase = SyncProductUseCase(
        coinBaseRepository
    )

    @Test
    fun handles_sync_product_data_action() {
        assertThat(createUseCase().canProcess(SyncProductUseCase.SyncProductData)).isTrue()
        assertThat(createUseCase().canProcess(object : Action {})).isFalse()
    }

    @Test
    fun handle_action__sync_product_data__instructs_repo_to_sync() {
        val coinBaseRepository: CoinBaseRepository = mockk(relaxUnitFun = true)
        val useCase = createUseCase(coinBaseRepository)

        var actualResult: SyncProductUseCase.ProductSyncComplete? = null
        runBlocking {
            useCase.handleAction(SyncProductUseCase.SyncProductData).collect {
                actualResult = it as SyncProductUseCase.ProductSyncComplete
            }
        }
        assertThat(actualResult).isEqualTo(SyncProductUseCase.ProductSyncComplete)

        verify {
            runBlocking {
                coinBaseRepository.syncProducts()
            }
        }

    }
}