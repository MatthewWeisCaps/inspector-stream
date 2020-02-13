# reactor-sireum

potential todo:

- override schedulers with virtual? https://projectreactor.io/docs/core/release/reference/#scheduler-factory
- Hide complex/unintuitive methods from Flux/Mono
  - focus on sync methods for control flow
  - hide or assist with methods that can cause races
- Default all scheduled operations to virtual time (?)
- CacheFlux (see extras) with persistence
- add MathFlux/MonoBoolean utils and helper methods