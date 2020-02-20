//name := "reactor-sireum"
//
//version := "0.1"
//
//scalaVersion := "2.12.7"
//
//Test / parallelExecution := true
//
//resolvers ++= Seq(Resolver.sonatypeRepo("public"), "jitpack" at "https://jitpack.io")
//resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases" // for scalatest
//resolvers += "Spring Repo" at "https://repo.spring.io/milestone"
//resolvers += "Jitpack" at "https://jitpack.io"
//
//libraryDependencies += "com.github.MatthewWeisCaps" % "inspector-bom" % "61410b2133"
//
//libraryDependencies += "io.projectreactor" % "reactor-core" % "3.3.2.RELEASE"
//libraryDependencies += "io.projectreactor" % "reactor-test" % "3.3.2.RELEASE" % Test
//
//libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"
//
////libraryDependencies += "javax.annotation" % "javax.annotation-api" % "1.3.2" // Javax annotations are used in reactor and cause fatal warnings for scala to parse. See for example @Nullable in package reactor.util.annotation
//libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.2" // except javax nullable is located here: https://stackoverflow.com/questions/19030954/cant-find-nullable-inside-javax-annotation
//
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0-M1" % Test
//
//libraryDependencies += "org.scalatest" %% "scalatest-freespec" % "3.2.0-M1" % Test
//
//libraryDependencies += "org.testng" % "testng" % "7.0.0" % Test
//
//libraryDependencies += "org.mockito" %% "mockito-scala" % "1.10.4" % Test
//
//// mockito-inline prevents errors when mocking final classes
//libraryDependencies += "org.mockito" % "mockito-inline" % "3.2.4" % Test
//
////addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3") // add runs to sbt
////logBuffered in Test := false
//
//libraryDependencies += "org.junit.jupiter" % "junit-jupiter-api" % "5.5.2"
//
//libraryDependencies += "org.reactivestreams" % "reactive-streams-tck" % "1.0.3"
//libraryDependencies += "org.reactivestreams" % "reactive-streams" % "1.0.3"