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
    val requestActivity: Activity = request.body
    val conn = DB.getConnection()
    var name: String = ""
    var location: String = ""
    var cost: Option[Double] = Option(1.00)
    var description: String = ""
    var complete: Boolean = true
    var activity: Activity = Activity(None, name, location, None, description, complete)
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate(s"INSERT INTO activities (name, location, cost, description, complete)" +
        s" VALUES (${requestActivity.name}, ${requestActivity.location}, ${requestActivity.cost}, ${requestActivity.description}, ${requestActivity.complete})")
      val autoId = stmt.getGeneratedKeys.getInt(1)
      val fetchFromDB = stmt.executeQuery(s"SELECT * FROM activities WHERE id=$autoId")
      while (fetchFromDB.next) {
        name = fetchFromDB.getString("name")
        location = fetchFromDB.getString("location")
        cost = Option(fetchFromDB.getFloat("cost").toDouble)
        description = fetchFromDB.getString("description")
        complete = fetchFromDB.getBoolean("complete")
        activity = Activity(Option(autoId), name, location, cost, description, complete)
      }

    } finally {
      conn.close()
    }
    Ok(Json.toJson(activity))
  }


}
