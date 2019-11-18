package example.data

import io.getquill.NamingStrategy

/**
 * A simple generator of SQL Schemas from Scala Case Classes using Reflection
 */
class ExampleTableMaker(namingStrategy: NamingStrategy) {

  import scala.reflect.runtime.universe._

  sealed trait DDLDataType { def render:String }
  object DDLDataType {
    def fromType(tpe: Type) =
      if (tpe =:= typeOf[String]) SqlVarchar
      else if (tpe =:= typeOf[Int]) SqlInt
      else throw new IllegalArgumentException()
  }

  case object SqlVarchar extends DDLDataType { val render = "VARCHAR(255)" }
  case object SqlInt extends DDLDataType { val render = "INT" }
  case class FieldDDL(name:String, dataType: DDLDataType) {
    def render = s"$name ${dataType.render}"
  }
  case class TableDDL(name:String, fields: List[FieldDDL]) {
    def render = s"CREATE TABLE $name (${fields.map(_.render).mkString(", ")});"
  }

  private def makeTableDDL[T](implicit tt: TypeTag[T]) = {
    val name = namingStrategy.table(tt.tpe.typeSymbol.name.toString)

    val fields =
      tt.tpe.members.sorted.collect {
        case m: MethodSymbol if (m.isCaseAccessor) =>
          FieldDDL(namingStrategy.column(m.name.toString), DDLDataType.fromType(m.returnType))
      }

    TableDDL(name, fields)
  }

  def apply[T](implicit tt: TypeTag[T]):String = {
    makeTableDDL[T].render
  }
}
