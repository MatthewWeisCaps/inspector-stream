name := "reactor-sireum"

version := "0.1"

scalaVersion := "2.12.7"

val sireumScalacVersion = "3.3.3"
val runtimeVersion = "88b726350a5e3658cb10f5f115c3f214112508ad"
incOptions := incOptions.value.withLogRecompileOnMacro(false)

scalacOptions := Seq("-target:jvm-1.8", "-deprecation", "-Ydelambdafy:method", "-feature", "-unchecked", "-Xfatal-warnings") // disabled Xfatal-warnings due to annotation reading bug
Test / parallelExecution := true

resolvers ++= Seq(Resolver.sonatypeRepo("public"), "jitpack" at "https://jitpack.io")
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases" // for scalatest
resolvers += "Spring Repo" at "https://repo.spring.io/milestone"

addCompilerPlugin("org.sireum" %% "scalac-plugin" % sireumScalacVersion)
libraryDependencies += "org.sireum.runtime" %% "library" % runtimeVersion

libraryDependencies += "io.projectreactor" % "reactor-core" % "3.3.0.M3"
libraryDependencies += "io.projectreactor.addons" % "reactor-extra" % "3.3.0.M1"
//libraryDependencies += "io.projectreactor" % "reactor-test" % "3.3.0.M3" //% Test // no longer needed! now directly include test

libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"

//libraryDependencies += "javax.annotation" % "javax.annotation-api" % "1.3.2" // Javax annotations are used in reactor and cause fatal warnings for scala to parse. See for example @Nullable in package reactor.util.annotation
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.2" // except javax nullable is located here: https://stackoverflow.com/questions/19030954/cant-find-nullable-inside-javax-annotation

//libraryDependencies += "io.projectreactor" %% "reactor-scala-extensions" % "0.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3") // add runs to sbt
logBuffered in Test := false

Compile / unmanagedSourceDirectories += baseDirectory.value / "src/main/reactor"
Test / unmanagedSourceDirectories += baseDirectory.value / "src/test/reactor"
//scalaSource in Compile := baseDirectory.value / "src/main/reactor"
//scalaSource in Test := baseDirectory.value / "src/test/reactor"

//libraryDependencies += "org.reactivestreams" % "reactive-streams-tck-flow" % "1.0.3"
libraryDependencies += "org.reactivestreams" % "reactive-streams-tck" % "1.0.3"
//libraryDependencies += "org.reactivestreams" % "reactive-streams-flow" % "1.0.3"
libraryDependencies += "org.reactivestreams" % "reactive-streams" % "1.0.3"