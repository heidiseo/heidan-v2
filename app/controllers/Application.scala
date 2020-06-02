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
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      val rs = stmt.executeQuery("SELECT tick FROM ticks")

      while (rs.next) {
        out += "Read from DB: " + rs.getTimestamp("tick") + "\n"
      }
    } finally {
      conn.close()
    }
    Ok(out)
  }
}
