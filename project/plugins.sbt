addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.10.1")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.1.0")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")
addSbtPlugin("io.spray"     % "sbt-revolver"  % "0.9.1")

addSbtPlugin("org.scalameta"    % "sbt-scalafmt"        % "2.4.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.4")
addSbtPlugin("ch.epfl.scala"    % "sbt-scalafix"        % "0.10.1")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"         % "0.5.1")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
