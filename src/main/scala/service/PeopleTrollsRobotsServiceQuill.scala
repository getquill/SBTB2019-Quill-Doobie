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

import scala.language.reflectiveCalls

class PeopleTrollsRobotsServiceQuill(xa: Transactor[IO]) extends Http4sDsl[IO] {

  object KillerRobotFlag extends FlagQueryParamMatcher("killer")

  // Uses H2 Dialect and $1, $2 etc... for variables
  //val ctx = new DoobieContext.H2[SnakeCase](SnakeCase)

  // Uses Postgres Dialect and ? for variables
  val ctx = new DoobieContext.Postgres[SnakeCase](SnakeCase)
  import ctx._

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "quill" / "peopleWhoAre" / role / "named" / name =>
      Ok(stream(joinTables(lift(role), query[Person].filter(_.firstName == lift(name))))
        .transact(xa).map(_.asJson))

    case GET -> Root / "quill" / "trollsWhoAre" / role / "in" / state =>
      val trollsInState = quote {
        for {
          t <- query[Troll]
          a <- query[Address].join(a => (t.id == a.ownerFk) && (a.state == lift(state)))
        } yield t
      }
      Ok(run {
        joinTables(lift(role), trollsInState).filter { case (_, _, perm) => perm != "Loudmothing" }
      }.transact(xa).map(_.asJson))

    case GET -> Root / "quill" / "robotsWhoAre" / role / "aged" / IntVar(years) :? KillerRobotFlag(asa) =>
      val robotsAged = quote { query[Robot].filter(rb => rb.age <= lift(years)) }
      val result =
        if (asa)
          stream(joinTables[Robot](lift(role), robotsAged))
        else
          stream(joinTables[Robot](lift(role), robotsAged).filter { case (_, roleName, _) => roleName != "Assassin"})

      Ok(result.transact(xa).map(_.asJson))
  }

  def joinTables[T <: {def id : Int}] =
    quote {
      (role: String, tbl: Query[T]) =>
        for {
          t <- tbl
          ur <- query[UserRole].join(ur => ur.userFk == t.id)
          r <- query[Role].join(r => r.id == ur.roleFk && r.name == role)
          rp <- query[RolePermission].join(rp => rp.roleFk == r.id)
          p <- query[Permission].join(p => rp.permissionFk == p.id)
        } yield (t, r.name, p.name)
    }
}
