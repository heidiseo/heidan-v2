package controllers

import model.Activity

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
//import play.api._
//import play.api.mvc._
//import play.api.cache.Cache
import play.api.Play.current
import play.api.db._
import play.api.libs.json.Json

import play.api.mvc._
import spray.json._
//import spray.json.DefaultJsonProtocol._

object Application extends Controller with DefaultJsonProtocol {

  implicit val activityFormat = Json.format[Activity]

  def getAll = Action {
    var id: Int = 1
    var name: String = ""
    var location: String = ""
    var cost: Option[Double] = Option(1.00)
    var description: String = ""
    var complete: Boolean = true
    var activity: ListBuffer[Activity] = ListBuffer()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val activities = stmt.executeQuery("SELECT * FROM activities")

      while (activities.next) {
        id = activities.getInt("id")
        name = activities.getString("name")
        location = activities.getString("location")
        cost = Option(activities.getFloat("cost").toDouble)
        description = activities.getString("description")
        complete = activities.getBoolean("complete")
        activity += Activity(id, name, location, cost, description, complete)
      }
    } finally {
      conn.close()
    }
    Ok(Json.toJson(activity))
  }
}
