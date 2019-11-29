lazy val commonSettings = Seq(
  name := "Quill Doobie Http4s",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    //"-Xfatal-warnings",
    //"-Ywarn-value-discard",
    "-Xlint:missing-interpolator"
  ),
)

lazy val Http4sVersion = "0.20.11"

lazy val DoobieVersion = "0.8.4"

lazy val H2Version = "1.4.197"

lazy val FlywayVersion = "5.2.4"

lazy val CirceVersion = "0.9.3"

lazy val PureConfigVersion = "0.10.2"

lazy val LogbackVersion = "1.2.3"

lazy val `quill-doobie-http4s` = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.http4s"            %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"            %% "http4s-circe"         % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"           % Http4sVersion,
      "org.http4s"            %% "http4s-blaze-client"  % Http4sVersion     % "it,test",

      "org.postgresql" % "postgresql" % "42.2.8",

      "org.tpolecat"          %% "doobie-core"          % DoobieVersion,
      "org.tpolecat"          %% "doobie-h2"            % DoobieVersion,
      "org.tpolecat"          %% "doobie-hikari"        % DoobieVersion,

      "com.h2database"        %  "h2"                   % H2Version,

      "org.flywaydb"          %  "flyway-core"          % FlywayVersion,

      "io.getquill"           %% "quill-jdbc"           % "3.5.0",
      "org.tpolecat"           %% "doobie-quill"        % "0.8.4",

      "io.circe"              %% "circe-generic"        % CirceVersion,
      "io.circe"              %% "circe-literal"        % CirceVersion      % "it,test",
      "io.circe"              %% "circe-optics"         % CirceVersion      % "it",

      "com.github.pureconfig" %% "pureconfig"           % PureConfigVersion,

      "ch.qos.logback"        %  "logback-classic"      % LogbackVersion
    )
  )