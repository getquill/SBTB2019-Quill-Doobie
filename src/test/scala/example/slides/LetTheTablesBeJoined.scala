package example.slides

import example.data.ExampleDataMaker
import model._
import doobie.implicits._

object LetTheTablesBeJoined {

  import example.db.Contexts._

  def main(args:Array[String]):Unit = {
    exampleCode()
  }

  def exampleCode(): Unit = {
    ExampleDataMaker.makeDataForH2()

    // ================= For larger queries, composition becomes important =================
    def namedPeopleWithRole(name: String, role: String) =
      sql"""
         select u.*, r.name, p.name
         from Person u
         join User_Role ur on ur.user_Fk = u.id
         join Role r on r.id = ur.role_Fk and r.name = ${role}
         join Role_Permission rp on rp.role_Fk = r.id
         join Permission p on p.id = rp.permission_Fk
         where u.first_name = ${name}
         """.query[(Person, String, String)].to[List].transact(xaH2)

    println(namedPeopleWithRole("Joe", "Drinking").unsafeRunSync() )


    def namedTrollsWithRole(state: String, role: String, loudmouthingAllowed: Boolean) =
      (fr"""
         select u.*, r.name, p.name
         from Troll u
         join User_Role ur on ur.user_Fk = u.id
         join Role r on r.id = ur.role_Fk and r.name = ${role}
         join Role_Permission rp on rp.role_Fk = r.id
         join Permission p on p.id = rp.permission_Fk""" ++
         (if (loudmouthingAllowed) fr"and p.name != 'Loudmouthing'" else fr"") ++
         fr"""
         join Address a on a.owner_Fk = u.id and a.state = ${state}
         """).query[(Troll, String, String)].to[List].transact(xaH2)

    println( namedTrollsWithRole("WI", "Drinker", false).unsafeRunSync() )


    def agedRobotsWithRole(age: Int, role: String, assassinationAllowed: Boolean) =
      (fr"""
         select u.*, r.name, p.name
         from Troll u
         join User_Role ur on ur.user_Fk = u.id
         join Role r on r.id = ur.role_Fk and r.name = ${role}
         join Role_Permission rp on rp.role_Fk = r.id
         join Permission p on p.id = rp.permission_Fk""" ++
        (if (assassinationAllowed) fr"and p.name != 'Assassination'" else fr"") ++
        fr"""
         where u.age = ${age}
         """).query[(Troll, String, String)].to[List].transact(xaH2)

    println( agedRobotsWithRole(100, "Assassin", true).unsafeRunSync() )


  }
}
