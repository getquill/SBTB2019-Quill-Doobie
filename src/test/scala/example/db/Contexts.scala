package example.db

import cats.effect.IO
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.{H2Dialect, SnakeCase, SqlMirrorContext}

object Contexts {
  private implicit val cs = IO.contextShift(scala.concurrent.ExecutionContext.global)

  val xaPostgres = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5432/quill_doobie",
    user = "sa",
    pass = "sa"
  )

  val xaH2 = Transactor.fromDriverManager[IO](
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:todo;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
  )

  val h2Context = new DoobieContext.H2[SnakeCase](SnakeCase)
  val postgresContext = new DoobieContext.Postgres[SnakeCase](SnakeCase)
  val mirrorH2Context = new SqlMirrorContext(H2Dialect, SnakeCase)
}
