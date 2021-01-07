name := "excercise2"

version := "0.1"

scalaVersion := "2.12.2"
val anormVersion = "2.5.3"

//libraryDependencies +=("org.playframework.anorm" %% "anorm" % "2.6.4")
//
//libraryDependencies += "com.typesafe.play" % "play-jdbc_2.10" % "2.4.3"
//
//libraryDependencies += ("org.scala-lang" % "scala-compiler" % scalaVersion.value % "test").exclude("org.scala-lang.modules", s"scala-xml_${scalaVersion.value}")
//libraryDependencies += "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5"

libraryDependencies += "com.typesafe.play" %% "anorm" % anormVersion
libraryDependencies += "com.typesafe.play" %% "anorm-akka" % anormVersion
libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.6"
//libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "2.5.2"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "3.5.0"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.13"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.0"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-config" % "3.3.5"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

//Thanks for using https://jar-download.com