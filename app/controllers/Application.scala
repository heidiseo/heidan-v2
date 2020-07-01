package controllers

import model.Activity
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
    val activity = Activity(1, "bike ride", "hyde park", None, "bike ride in hyde park", true)
    Ok(Json.toJson(activity))
  }

  def db = Action {
    var id: Int = 1
    var name: String = ""
    var location: String = ""
    var cost: Option[Double] = Option(1.00)
    var description: String = ""
    var complete: Boolean = true
    var activity: Activity = Activity(id, name, location, cost, description, complete)
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

//      val idA = stmt.executeQuery("SELECT id FROM activities")
//      val nameA = stmt.executeQuery("SELECT name FROM activities")
//      val locationA = stmt.executeQuery("SELECT location FROM activities")
//      val costA = stmt.executeQuery("SELECT cost FROM activities")
//      val descriptionA = stmt.executeQuery("SELECT description FROM activities")
//      val activities = stmt.executeQuery("SELECT complete FROM activities")
      val activities = stmt.executeQuery("SELECT * FROM activities")

      while (activities.next) {
        id = activities.getInt("id")
        name = activities.getString("name")
        location = activities.getString("location")
        cost = Option(activities.getFloat("cost").toDouble)
        description = activities.getString("description")
        complete = activities.getBoolean("complete")
        activity = Activity(id, name, location, cost, description, complete)
      }
    } finally {
      conn.close()
    }
    Ok(Json.toJson(activity))
  }
}
