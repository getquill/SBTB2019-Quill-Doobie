package service

import cats.effect.IO
import doobie.implicits._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import model._
import io.circe.generic.auto._
import io.circe.syntax._
import io.getquill.SnakeCase
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class SimpleService(xa: Transactor[IO]) extends Http4sDsl[IO] {

  // Uses H2 Dialect and $1, $2 etc... for variables
  //val ctx = new DoobieContext.H2[SnakeCase](SnakeCase)

  // Uses Postgres Dialect and ? for variables
  val ctx = new DoobieContext.Postgres[SnakeCase](SnakeCase)
  import ctx._

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "simple" / "doobie" / "peopleNamed" / name =>
      Ok(sql"select * from Person where first_name = ${name}"
        .query[Person].to[List].transact(xa).map(_.asJson))

    case GET -> Root / "simple" / "quill" / "peopleNamed" / name =>
      Ok(run {
        query[Person].filter(_.firstName == lift(name))
      }.transact(xa).map(_.asJson))

    case GET -> Root / "simple" / "doobie" / "trollsIn" / state =>
      Ok(sql"select t.* from Troll t join Address a on a.owner_fk = t.id and a.state = ${state}"
        .query[Troll].stream.transact(xa).map(_.asJson))

    case GET -> Root / "simple" / "quill" / "trollsIn" / state =>
      Ok(stream {
        for {
          t <- query[Troll]
          a <- query[Address] if (t.id == a.ownerFk) && (a.state == lift(state))
        } yield t
      }.transact(xa).map(_.asJson))

    case GET -> Root / "simple" / "doobie" / "robotsInService" / IntVar(years) =>
      Ok(sql"select * from Robot where age > ${years}"
        .query[Robot].stream.transact(xa).map(_.asJson))

    case GET -> Root / "simple" / "quill" / "robotsInService" / IntVar(years) =>
      Ok(stream {
        query[Robot].filter(r => r.age > lift(years))
      }.transact(xa).map(_.asJson))
  }

}
