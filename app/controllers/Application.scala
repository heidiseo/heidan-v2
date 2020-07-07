package controllers


import java.sql.SQLException

import model.Activity

import scala.collection.mutable.ListBuffer
import play.api.Play.current
import play.api.db._
import play.api.libs.json.Json
import play.api.mvc._
import spray.json._

object Application extends Controller with DefaultJsonProtocol {

  implicit val activityFormat = Json.format[Activity]

  def getAll(completed: Option[Boolean]) = Action {
    var id: Int = 1
    var name: String = ""
    var location: String = ""
    var cost: Option[Double] = Option(1.00)
    var description: String = ""
    var complete: Boolean = true
    var activities: ListBuffer[Activity] = ListBuffer()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val activitiesFromDB = stmt.executeQuery("SELECT * FROM activities")

      while (activitiesFromDB.next) {
        id = activitiesFromDB.getInt("id")
        name = activitiesFromDB.getString("name")
        location = activitiesFromDB.getString("location")
        cost = Option(activitiesFromDB.getFloat("cost").toDouble)
        description = activitiesFromDB.getString("description")
        complete = activitiesFromDB.getBoolean("complete")
        activities += Activity(Option(id), name, location, cost, description, complete)
      }
    } finally {
      conn.close()
    }
    Ok(
      completed match {
        case Some(true) => Json.toJson(activities.filter(_.complete))
        case Some(false) => Json.toJson(activities.filterNot(_.complete))
        case _ => Json.toJson(activities)
      })
  }

  def getById(inputId: Int): Action[AnyContent] = Action {
    var name: String = ""
    var location: String = ""
    var cost: Option[Double] = Option(1.00)
    var description: String = ""
    var complete: Boolean = true
    var activity: Activity = Activity(Option(inputId), name, location, None, description, complete)
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val activitiesFromDB = stmt.executeQuery(s"SELECT * FROM activities WHERE id=$inputId")

      while (activitiesFromDB.next) {
        name = activitiesFromDB.getString("name")
        location = activitiesFromDB.getString("location")
        cost = Option(activitiesFromDB.getFloat("cost").toDouble)
        description = activitiesFromDB.getString("description")
        complete = activitiesFromDB.getBoolean("complete")
        activity = Activity(Option(inputId), name, location, cost, description, complete)
      }
    } finally {
      conn.close()
    }
    Ok(Json.toJson(activity))
  }

  def create(): Action[Activity] = Action(parse.json[Activity]) { request: Request[Activity] =>
    val activity: Activity = request.body
    val conn = DB.getConnection()
    var autoId = 0
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate(s"INSERT INTO activities (name, location, cost, description, complete)" +
        s" VALUES (${activity.name}, ${activity.location}, ${activity.cost}, ${activity.description}, ${activity.complete})")
      autoId = stmt.getGeneratedKeys.getInt(1)

    } finally {
      conn.close()
    }
    Ok(getById(autoId))
  }


}
