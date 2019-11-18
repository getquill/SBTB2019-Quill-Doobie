package example.slides

import example.db.Contexts
import model._

import scala.language.existentials

object QuillCaveats {

  import Contexts.mirrorH2Context._

  def simpleQuery() = {
    case class Person(name: String, age: Int)
    val m = run(query[Person].filter(p => p.name == "Joe"))
  }

  def needToLift() = {
    case class Person(name: String, age: Int)
    def peopleNamed(name: String) = quote {
      query[Person].filter(p => p.name == lift(name))
    }

    run(peopleNamed("Joe"))
  }

  def needToLift_Better() = {
    case class Person(name: String, age: Int)
    val peopleNamed = quote {
      (name: String) =>
        query[Person].filter(p => p.name == name) // should not be lifted. Need better way to identify that
    }

    run(peopleNamed("Joe"))
  }

  def runtimeConditionIssue() = {
    def runtimeChoice(runtimeCondition: Boolean) =
      if (runtimeCondition)
        quote { query[Person].filter(p => p.firstName == "Joe") }
      else
        quote { query[Person].filter(p => p.firstName == "Jack") }

    val output = run(runtimeChoice(true))
    println(output.string)
  }

  def useCompileTimeCondition() = {
    val runtimeCondition = true

    val people = quote { query[Person] }

    val compileTimeComposition = quote {
      (q: Query[Person], filterValue: String) =>
        q.filter(p => p.firstName == filterValue)
    }

    val myOutput =
      if (runtimeCondition)
        run(compileTimeComposition(people, "Joe"))
      else
        run(compileTimeComposition(people, "Jack"))

    println(myOutput)
  }

  def runtimeWideningIssue() = {
    val q: Quoted[Query[Person]] = quote { query[Person] }
    val output = run(q)
    println(output.string)
  }

  def main(args: Array[String]):Unit = {
    needToLift()
    needToLift_Better()
    useCompileTimeCondition()
    runtimeConditionIssue()
  }
}
