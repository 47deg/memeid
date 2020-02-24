ThisBuild / scalaVersion       := "2.13.1"
ThisBuild / crossScalaVersions := Seq("2.12.10", "2.13.1")
ThisBuild / organization       := "com.47deg"

Global / onChangedBuildSource := ReloadOnSourceChanges

addCommandAlias("ci-test", "fix --check; +docs/mdoc; +test")
addCommandAlias("ci-docs", "+docs/mdoc; headerCreateAll")

lazy val `root` = project
  .in(file("."))
  .aggregate(allProjects: _*)
  .settings(skip in publish := true)

lazy val `docs` = project
  .in(file("memeid-docs"))
  .settings(name := "memeid")
  .enablePlugins(MdocPlugin)
  .settings(mdocOut := file("."))
  .settings(skip in publish := true)
  .settings(dependencies.docs)
  .dependsOn(allProjects.map(ClasspathDependency(_, None)): _*)

lazy val `memeid` = project
  .settings(crossPaths := false)
  .settings(publishMavenStyle := true)
  .settings(autoScalaLibrary := false)
  .settings(dependencies.common)

lazy val memeid4s = project
  .dependsOn(`memeid`, `memeid4s-scalacheck` % Test)
  .settings(dependencies.common)

lazy val `memeid4s-cats` = project
  .dependsOn(`memeid4s`, `memeid4s-scalacheck` % Test)
  .settings(dependencies.common, dependencies.cats)

lazy val `memeid4s-literal` = project
  .dependsOn(`memeid4s`)
  .settings(dependencies.common, dependencies.literal)

lazy val `memeid4s-doobie` = project
  .dependsOn(`memeid4s`)
  .settings(dependencies.common, dependencies.doobie)

lazy val `memeid4s-circe` = project
  .dependsOn(`memeid4s`, `memeid4s-cats` % Test, `memeid4s-scalacheck` % Test)
  .settings(dependencies.common, dependencies.circe)

lazy val `memeid4s-http4s` = project
  .dependsOn(`memeid4s`, `memeid4s-cats` % Test, `memeid4s-scalacheck` % Test)
  .settings(dependencies.common, dependencies.http4s)

lazy val `memeid4s-scalacheck` = project
  .dependsOn(memeid)
  .settings(dependencies.scalacheck)

lazy val allProjects: Seq[ProjectReference] = Seq(
  `memeid`,
  memeid4s,
  `memeid4s-cats`,
  `memeid4s-literal`,
  `memeid4s-doobie`,
  `memeid4s-circe`,
  `memeid4s-http4s`,
  `memeid4s-scalacheck`
)
