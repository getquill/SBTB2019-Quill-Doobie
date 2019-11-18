package example.data

import cats.effect.IO
import doobie.quill.DoobieContextBase
import doobie._
import doobie.implicits._
import cats.implicits._
import example.db.Contexts
import io.getquill.context.sql.idiom.SqlIdiom
import io.getquill.{H2Dialect, NamingStrategy, PostgresDialect, SnakeCase}
import model._

/**
 * A simple generator of example data in case you want to experiment with the model.
 * This was used to generate the contents of: `V1__create_data.sql`
 */
object ExampleDataMaker {

  val people = List(
    Person(1, "Jack", "Ripper", 55),
    Person(2, "Vlad", "Dracul", 321),
    Person(3, "Joe", "Bloggs", 20)
  )

  val trolls = List(
    Troll(50, "Spammmalot", "Headachemaker", 23),
    Troll(51, "Flabbergasto", "Poopoomouth", 24),
    Troll(52, "Aggrevatus", "Keybreaker", 24)
  )

  val addresses = List(
    Address(50, "Summer St", 11111, "NY"),
    Address(51, "Winter St", 11111, "NY"),
    Address(52, "Super St", 11111, "WI")
  )

  val robots = List(
    Robot(100,"Terminator", 300),
    Robot(101,"Wall-E", 250)
  )

  val userRoles =
    List(
      UserRole(1111, 1, 1), UserRole(1112, 1, 3), // Jack is a partygoer and a assasin, he cannot drink
      UserRole(1113, 2, 1), UserRole(1114, 2, 2), UserRole(1111, 2, 4),  // Valad is a partygoer, he can drink and can impale
      UserRole(1115, 3, 1),                       // Joe is just a partygoer, he cannot do anything else

      UserRole(1116, 50, 1), UserRole(1117, 50, 2), UserRole(1117, 50, 6), // Trolls can dance, drink and loudmouth
      UserRole(1118, 51, 1), UserRole(1117, 51, 2), UserRole(1119, 51, 6), // Trolls can dance, drink and loudmouth
      UserRole(1120, 52, 1), UserRole(1117, 52, 2), UserRole(1121, 52, 6), // Trolls can dance, drink and loudmouth

      UserRole(1122, 100, 1), UserRole(1123, 100, 2), UserRole(1124, 100, 3), // The Terminator is an Assasin and he can drink
      UserRole(1125, 101, 5),                     // Wall-E is just a custodian
    )
  val roles = List(Role(1, "Partygoer"), Role(2, "Drinker"), Role(3, "Assassin"), Role(4, "Impaler"), Role(5, "Custodian"), Role(6, "Loudmouth"))
  val rolePermissions =
    List(
      RolePermission(2222, 1, 1), // Partygoers can dance
      RolePermission(2223, 2, 2), // Drinkers can drink
      RolePermission(2224, 3, 3), // Assasins can assasinate and impale
      RolePermission(2225, 3, 4),
      RolePermission(2226, 4, 4), // Impalers can just impale
      RolePermission(2227, 5, 5),  // Custodians can clean
      RolePermission(2228, 6, 6)  // Loud-mouths can name-call
    )
  val permission = List(
    Permission(1, "Dancing"), Permission(2, "Drinking"), Permission(3, "Impaling"),
    Permission(4, "Assassinating"), Permission(5, "Cleaning"), Permission(6, "Name Calling"))

  trait DataMaker[I <: SqlIdiom, N <: NamingStrategy] {
    val ctx:DoobieContextBase[I, N]
    val xa: Transactor[IO]

    import ctx._

    def makeData() = {
      val creator = new ExampleTableMaker(SnakeCase)
      val createStatements = List(
        creator[Person], creator[Robot], creator[Address],
        creator[Troll], creator[UserRole], creator[Role], creator[RolePermission], creator[Permission]
      )
      val create = createStatements.mkString("\n")
      println(s"====Creating Tables====:\n${create}")
      val result = Fragment.const(create).update.run.transact(xa).unsafeRunSync()
      println("====Results Of Operation====\n" + result)



      val combined =
        List(
          run(liftQuery(ExampleDataMaker.people).foreach(r => query[Person].insert(r))).transact(xa),
          run(liftQuery(ExampleDataMaker.robots).foreach(r => query[Robot].insert(r))).transact(xa),
          run(liftQuery(ExampleDataMaker.addresses).foreach(r => query[Address].insert(r))).transact(xa),
          run(liftQuery(ExampleDataMaker.trolls).foreach(r => query[Troll].insert(r))).transact(xa),
          run(liftQuery(ExampleDataMaker.userRoles).foreach(r => query[UserRole].insert(r))).transact(xa),
          run(liftQuery(ExampleDataMaker.roles).foreach(r => query[Role].insert(r))).transact(xa),
          run(liftQuery(ExampleDataMaker.rolePermissions).foreach(r => query[RolePermission].insert(r))).transact(xa),
          run(liftQuery(ExampleDataMaker.permission).foreach(r => query[Permission].insert(r))).transact(xa)
        ).sequence

      combined.unsafeRunSync()
    }
  }

  def makeDataForTransactor(ts: Transactor[IO]) = {
    implicit val cs = IO.contextShift(scala.concurrent.ExecutionContext.global)

    val maker = new DataMaker[H2Dialect, SnakeCase] {
      override val ctx = Contexts.h2Context
      override val xa = ts
    }
    println(maker.makeData())
  }


  def makeDataForH2() = {
    val maker = new DataMaker[H2Dialect, SnakeCase] {
      override val ctx = Contexts.h2Context
      override val xa = Contexts.xaH2
    }
    println(maker.makeData())
  }

  def makeDataForPostgres() = {
    val maker = new DataMaker[PostgresDialect, SnakeCase] {
      override val ctx = Contexts.postgresContext
      override val xa = Contexts.xaPostgres
    }
    println(maker.makeData())
  }

  def main(args: Array[String]):Unit = {
    makeDataForPostgres()
    //makeDataForH2()
  }
}
