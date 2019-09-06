This package contains an almost-identical copy of the reactor-test 3.3.0.M3 library, with a couple small changes to
make calling java vararg functions easier in scala. All changes are noted in this file.

UNMODIFIED VERSIONS OF THIS PACKAGE AND SUBPACKAGES BELONG TO THE REACTOR-CORE (TEST) (v. 3.3.0.M3) PROJECT

/*
 * Copyright (c) 2011-2019 Pivotal Software Inc, All Rights Reserved.
 *
 * With modifications from Santos Labs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

see: https://projectreactor.io/
see: https://github.com/reactor/reactor-core/

ALL MODIFICATIONS MADE WILL BE LISTED IN THIS README AS WELL AS IN A COMMENT WRITTEN NEXT TO EACH CHANGE.

======================
  MODIFICATIONS List
======================

StepVerifier.java - line 726 - renamed method "expectNext(T... ts)" to "expectNextValues(T... ts)" (helps Scala avoid vararg method ambiguity)
DefaultStepVerifierBuilder.java - line 479 - renamed method "expectNext(T... ts)" to "expectNextValues(T... ts)" (helps Scala avoid vararg method ambiguity)

StepVerifier.java - line 841 - renamed method "then(Runnable task)" to "thenRun(Runnable task)" because usage of "then" as an identifier was depreciated in scala 2.10.0
DefaultStepVerifierBuilder.java - line 615 - renamed method "then(Runnable task)" to "thenRun(Runnable task)" because usage of "then" as an identifier was depreciated in scala 2.10.0

util.VirtualTimeScheduler.java - line 328 - added clause to loop drain for VirtualTimeSchedulers at the epoch
