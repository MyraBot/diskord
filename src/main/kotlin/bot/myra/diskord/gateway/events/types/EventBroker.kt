package bot.myra.diskord.gateway.events.types

import bot.myra.diskord.rest.behaviors.DiskordObject

abstract class EventBroker : EventAction(), DiskordObject {

    final override suspend fun handle() {
        choose()?.handle()
    }

    abstract suspend fun choose(): EventAction?

}