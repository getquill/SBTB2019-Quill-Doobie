package object model {
  case class Person(id:Int, firstName:String, lastName:String, age:Int)
  case class Troll(id:Int, firstName:String, lastName:String, age:Int)
  case class Robot(id:Int, name:String, age:Int)
  case class Address(ownerFk:Int, street:String, zip:Int, state:String)

  case class UserRole(id:Int, userFk:Int, roleFk:Int)
  case class Role(id:Int, name:String)
  case class RolePermission(id:Int, roleFk:Int, permissionFk:Int)
  case class Permission(id:Int, name:String)
}
