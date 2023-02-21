package bot.myra.diskord.gateway.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ForkJoinPool

object Events {

    val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher())

}