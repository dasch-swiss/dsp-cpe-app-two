val Version = new {
  val Scala      = "2.13.8"
  val ScalaGroup = "2.13"

  val zio     = "2.0.0"
  val zioJson = "0.3.0-RC9"
  val zioHttp = "2.0.0-RC10"

  val laminar             = "0.14.2"
  val laminext            = "0.14.3"
  val scalaJsSecureRandom = "1.0.0"
  val scalaJavaTime       = "2.3.0"

  val organiseImports = "0.5.0"
}

inThisBuild(
  List(
    organization                                   := "swiss.dasch",
    scalaVersion                                   := Version.Scala,
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % Version.organiseImports,
    semanticdbEnabled                              := true,
    semanticdbVersion                              := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion                     := Version.ScalaGroup,
  ),
)

Global / onChangedBuildSource := ReloadOnSourceChanges

val Dependencies = new {

  lazy val frontend = Seq(
    libraryDependencies ++= Seq(
      "com.raquo"         %%% "laminar"                   % Version.laminar,
      "io.laminext"       %%% "core"                      % Version.laminext,
      "io.laminext"       %%% "fetch"                     % Version.laminext,
      "io.laminext"       %%% "websocket"                 % Version.laminext,
      "org.scala-js"      %%% "scalajs-java-securerandom" % Version.scalaJsSecureRandom,
      "io.github.cquiroz" %%% "scala-java-time"           % Version.scalaJavaTime,
    ),
  )

  lazy val backend = Def.settings(
    libraryDependencies ++= Seq(
      "io.d11" %% "zhttp" % Version.zioHttp,
    ),
  )

  lazy val shared = Def.settings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio"          % Version.zio,
      "dev.zio" %%% "zio-json"     % Version.zioJson,
      "dev.zio" %%% "zio-streams"  % Version.zio,
      "dev.zio" %%% "zio-test"     % Version.zio % Test,
      "dev.zio" %%% "zio-test-sbt" % Version.zio % Test,
    ),
    testFrameworks ++= Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
  )
}

lazy val commonSettings = Seq()

lazy val commonBuildSettings = Seq(
  scalaVersion  := Version.Scala,
  scalacOptions := Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:higherKinds",
    "-language:existentials",
    "-unchecked",
    "-deprecation",
    "-Wunused:imports",
    "-Wvalue-discard",
    "-Wunused:patvars",
    "-Wunused:privates",
    // "-Wunused:params",
    "-Wvalue-discard",
    // "-Xfatal-warnings",
    "-explaintypes",
    "-Yrangepos",
    "-Xlint:_,-type-parameter-shadow",
    "-Xsource:2.13",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-unused",
  ),
)

lazy val root =
  (project in file(".")).aggregate(frontend, backend, shared.js, shared.jvm)

lazy val frontend = (project in file("modules/frontend"))
  .dependsOn(shared.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(commonBuildSettings)
  .settings(scalaJSUseMainModuleInitializer := true)
  .settings(
    Dependencies.frontend,
    Dependencies.shared,
    Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv,
  )
  .settings(
    Seq(
      buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, isSnapshot),
      buildInfoPackage := "swiss.dasch.cpe",
      buildInfoObject  := "BuildInfo",
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val backend = (project in file("modules/backend"))
  .dependsOn(shared.jvm)
  .settings(commonBuildSettings)
  .settings(Dependencies.backend)
  .settings(Dependencies.shared)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    Test / fork          := true,
    Universal / mappings += {
      val appJs = (frontend / Compile / fullOptJS).value.data
      appJs -> "lib/prod.js"
    },
    Universal / javaOptions ++= Seq(
      "--port 8080",
      "--mode prod",
    ),
    Docker / packageName := "dsp-cpe-server",
  )
  .settings(
    Seq(
      buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, isSnapshot),
      buildInfoPackage := "swiss.dasch.cpe",
      buildInfoObject  := "BuildInfo",
    ),
  )
  .enablePlugins(BuildInfoPlugin)

lazy val shared =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/shared"))
    .settings(commonBuildSettings)
    .settings(
      run / fork := true,
      Test / run / javaOptions += "-Djava.net.preferIPv4Stack=true",
      name       := "swiss.dasch.cpe",
    )
    .settings(Dependencies.shared)
    .jsSettings(
      scalaJSLinkerConfig ~= {
        _.withModuleKind(ModuleKind.ESModule)
      },
      scalaJSLinkerConfig ~= {
        _.withSourceMap(true)
      },
    )
    .settings(
      Seq(
        buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, isSnapshot),
        buildInfoPackage := "swiss.dasch.cpe",
        buildInfoObject  := "BuildInfo",
      ),
    )
    .enablePlugins(BuildInfoPlugin)

lazy val fastOptCompileCopy = taskKey[Unit]("")

val jsPath = "modules/backend/src/main/resources"

fastOptCompileCopy := {
  val source = (frontend / Compile / fastOptJS).value.data
  IO.copyFile(
    source,
    baseDirectory.value / jsPath / "dev.js",
  )
}

lazy val fullOptCompileCopy = taskKey[Unit]("")

fullOptCompileCopy := {
  val source = (frontend / Compile / fullOptJS).value.data
  IO.copyFile(
    source,
    baseDirectory.value / jsPath / "prod.js",
  )

}

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "ProcedureSyntax",
  "NoValInForComprehension",
).mkString(" ")

val CICommands = Seq(
  "clean",
  "backend/compile",
  "backend/test",
  "frontend/compile",
  "frontend/fastOptJS",
  "frontend/test",
  "scalafmtCheckAll",
  s"scalafix --check $scalafixRules",
).mkString(";")

val PrepareCICommands = Seq(
  s"compile:scalafix --rules $scalafixRules",
  s"test:scalafix --rules $scalafixRules",
  "test:scalafmtAll",
  "compile:scalafmtAll",
  "scalafmtSbt",
).mkString(";")

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
addCommandAlias("runDev", ";fastOptCompileCopy; backend/reStart --mode dev")
addCommandAlias("runProd", ";fullOptCompileCopy; backend/reStart --mode prod")
addCommandAlias("ci", CICommands)
addCommandAlias("prep", PrepareCICommands)
