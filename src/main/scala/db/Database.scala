package db

import cats.effect.{Blocker, ContextShift, IO, Resource}
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway

object Database {
  def create(implicit cs: ContextShift[IO]) = new Database
}

class Database(implicit cs: ContextShift[IO]) {
  def transactor(config: DatabaseConfig): Resource[IO, HikariTransactor[IO]] = {
    for {
      ec <- ExecutionContexts.fixedThreadPool[IO](32)
      be <- Blocker[IO]
      xa <- HikariTransactor.newHikariTransactor[IO](config.driver, config.url, config.user, config.password, ec, be)
    } yield (xa)
  }

  def initialize(transactor: HikariTransactor[IO]): IO[Unit] = {
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }
    }
  }
}
