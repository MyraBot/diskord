package bot.myra.diskord.gateway.events.types

abstract class EventBroker : EventAction() {

    final override suspend fun handle() {
        choose()?.handle()
    }

    abstract suspend fun choose(): EventAction?

}