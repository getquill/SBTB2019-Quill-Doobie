package service

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import model._
import fs2.Stream
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class PeopleTrollsRobotsService(xa: Transactor[IO]) extends Http4sDsl[IO] {

  object KillerRobotFlag extends FlagQueryParamMatcher("killer")

  def fromClause(
    role: String,
    disabledPermission: Option[String] = None,
    disabledRole: Option[String] = None
  ) =
    (fr"""
         join User_Role ur on ur.user_Fk = u.id
         join Role r on r.id = ur.role_Fk and r.name = ${role}
         join Role_Permission rp on rp.role_Fk = r.id
         join Permission p on p.id = rp.permission_Fk""" ++
      (if (disabledPermission.isDefined) Fragment.const(s"and p.name != '${disabledPermission.get}'") else fr"")++
      (if (disabledRole.isDefined) Fragment.const(s"and r.name != '${disabledRole.get}'") else fr"")
    )

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "doobie" / "peopleWhoAre" / role / "named" / name =>
      Ok(
        ((fr"""
         select u.*, r.name, p.name
         from Person u""" ++
         fromClause(role) ++
        fr"where u.first_name = ${name}"
      ).query[(Person, String, String)].stream)
          .transact(xa).map(_.asJson.toString())
      )

    case GET -> Root / "doobie" / "trollsWhoAre" / role / "in" / state =>
      Ok((fr"""
         select u.*, r.name, p.name
         from Troll u""" ++
         fromClause(role, Some("Loudmouthing")) ++
        fr"join Address a on a.owner_Fk = u.id and a.state = ${state}"
      ).query[(Troll, String, String)].stream.transact(xa).map(_.asJson))

    case GET -> Root / "doobie" / "robotsWhoAre" / role / "aged" / IntVar(years) :? KillerRobotFlag(assassinationAllowed) =>
      Ok((fr"""
         select u.*, r.name, p.name
         from Robot u""" ++
        fromClause(role, None, if (assassinationAllowed) None else Some("Assassin")) ++
        fr"where u.age <= ${years}"
      ).query[(Robot, String, String)].stream.transact(xa).map(_.asJson))
  }
}
