package example.slides

import doobie.util.fragment.Fragment
import model._
import example.data.ExampleDataMaker
import doobie.implicits._
import example.db.Contexts.xaH2
import cats.implicits._

object LetTheTablesBeJoinedAbstracted {

  def main(args:Array[String]):Unit = {
    exampleCode()
  }

  def exampleCode(): Unit = {
    ExampleDataMaker.makeDataForH2()

    // Can't use a "fr" here because the value may exist here or it may not

    def fromClause(role: String, disabledPermission: String, isDisabled: Boolean) =
      (fr"""
         join User_Role ur on ur.user_Fk = u.id
         join Role r on r.id = ur.role_Fk and r.name = ${role}
         join Role_Permission rp on rp.role_Fk = r.id
         join Permission p on p.id = rp.permission_Fk""" ++
      (if (isDisabled) Fragment.const(s"and p.name != '${disabledPermission}'") else fr""))

    def namedPeopleWithRole(name: String, role: String) =
      (fr"""
         select u.*, r.name, p.name
         from Person u""" ++
         fromClause(role, "<Doesn't Matter>", false) ++
         fr"where u.first_name = ${name}"
      ).query[(Person, String, String)].to[List].transact(xaH2)

    println(namedPeopleWithRole("Joe", "Drinker").unsafeRunSync() )


    def namedTrollsWithRole(state: String, role: String, loudmouthingAllowed: Boolean) =
      (fr"""
         select u.*, r.name, p.name
         from Troll u""" ++
         fromClause(role, "Loudmouthing", loudmouthingAllowed) ++
         fr"join Address a on a.owner_Fk = u.id and a.state = ${state}"
       ).query[(Troll, String, String)].to[List].transact(xaH2)

    println( namedTrollsWithRole("WI", "Drinker", false).unsafeRunSync() )


    def agedRobotsWithRole(age: Int, role: String, assassinationAllowed: Boolean) =
      (fr"""
         select u.*, r.name, p.name
         from Troll u""" ++
         fromClause(role, "Assassination", assassinationAllowed) ++
         fr"where u.age = ${age}"
      ).query[(Troll, String, String)].to[List].transact(xaH2)

    println( agedRobotsWithRole(100, "Drinker", true).unsafeRunSync() )


  }
}
