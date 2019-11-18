package example.slides

import model._
import scala.language.reflectiveCalls

object QuillMonadicJoins {

  import example.db.Contexts.mirrorH2Context._

  def wishWeHadFunction(): Unit = {

    def joinRolesAndPermissions[T <: {def id: Int}] = quote {
      (tbl: Query[T]) =>
        for {
          t  <- tbl
          ur <- query[UserRole] if (ur.userFk == t.id)
          r  <- query [Role] if (r.id == ur.roleFk)
          rp <- query[RolePermission] if (rp.roleFk == r.id)
          p  <- query[Permission] if (p.id == rp.permissionFk)
        } yield (t, r.name, p.name)
    }

    def joinRolesAndPermissionsApplicative2[T <: {def id: Int}] = quote {
      (tbl: Query[T]) =>
        tbl
          .join(query[UserRole]).on((t, ur) => ur.userFk == t.id)
          .join(query[Role]).on((tur, r) => r.id == tur._2.roleFk)
          .join(query[RolePermission]).on((turr, rp) => rp.roleFk == turr._2.id)
          .join(query[Permission]).on((turrrp, p) => p.id == turrrp._2.permissionFk)
          .map(turrrpp => (turrrpp._1._1._1._1, turrrpp._1._1._2.name, turrrpp._2.name))
    }
    val outputApp2 = quote { joinRolesAndPermissionsApplicative2(query[Person]) }
    run(outputApp2)


    def joinRolesAndPermissionsApplicative[T <: {def id: Int}] = quote {
      (tbl: Query[T]) =>
        tbl
          .join(query[UserRole])
              .on {case (t, ur) => ur.userFk == t.id}
          .join(query[Role])
              .on {case ((t, ur), r) => r.id == ur.roleFk}
          .leftJoin(query[RolePermission])
              .on {case (((t, ur), r), rp) => rp.roleFk == r.id}
          .leftJoin(query[Permission])
              .on {case ((((t, ur), r), rpo), p) => rpo.exists(rp => rp.permissionFk == p.id)}
          .map {case ((((t, ur), r), rp), p) => (t, r.name, p.map(_.name))}
    }
    val outputApp = quote { joinRolesAndPermissionsApplicative(query[Person]) }
    run(outputApp)


    def joinRolesAndPermissionsMonadic1[T <: {def id: Int}] = quote {
      (tbl: Query[T]) =>
        for {
          t  <- tbl
          ur <- query[UserRole].join(ur => ur.userFk == t.id)
          r  <- query [Role].join(r => r.id == ur.roleFk)
          rp <- query[RolePermission].join(rp => rp.roleFk == r.id)
          p  <- query[Permission].join(p => rp.permissionFk == p.id)
        } yield (t, r.name, p.name)
    }

    def joinRolesAndPermissionsMonadic2[T <: {def id: Int}] = quote {
      (tbl: Query[T]) =>
        for {
          t  <- tbl
          ur <- query[UserRole].join(ur => ur.userFk == t.id)
          r  <- query [Role].join(r => r.id == ur.roleFk)
          rpo <- query[RolePermission].leftJoin(rp => rp.roleFk == r.id)
          po  <- query[Permission].leftJoin(p => rpo.exists(rp => rp.permissionFk == p.id))
        } yield (t, r.name, po.map(_.name))
    }
    val outputAppM2 = quote { joinRolesAndPermissionsMonadic2(query[Person]) }
    run(outputAppM2)


    val output = quote { joinRolesAndPermissions(query[Person]) }
    run(output)

  }

}
