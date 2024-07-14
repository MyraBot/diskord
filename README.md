# Diskord

Diskord is a Discord wrapper written in Kotlin, making use of coroutines and Kotlins async model.

![Warning](https://raw.githubusercontent.com/MyraBot/.github/main/code-advise.png)

This library is not stable! The gateway connection isn't stable and errors with messages like these:

## Features

- [x] Gateway
- [x] Rest
- [x] Caching (Gateway + Rest)
- [x] Voice
- [ ] Sharding

## TODO

- [ ] Make all events `open` classes
- [ ] Name function `fetchX` to show that rest is being used
- [ ] Do not put `Interaction` in `InteractionModifier.kt` - but still make it available to use in myra for finding out the
  language
- [ ] Use normal Instant class instead of custom time class
- [ ] Use https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker
- [ ] Create copy of cache for every event and fire then every cache event and functions immediatly instead of waiting for all events to finish and updating the cache then

## Caching

Diskord has **Rest caching** as well as **Gateway caching**.
> Though note that if you turn of gateway caching the objects won't be in sync anymore since update events are missing!

## Event abstraction

If an even can be interpreted in different ways the event has the following structure

```
    FooEventBroker
          |
          V
    FooBarEvent ─┐
                 ├─ GenericFooEvent
    FooBazEvent ─┘
```

`FooEventBroker`: Event handler which calls `GenericFooEvent`s subclasses  
`GenericFooEvent`: Abstract class which shares common methods across its subclasses

Because the cache waits for all functions of the fired event to finish, the cache may be outdated for events following very
close on each other.
