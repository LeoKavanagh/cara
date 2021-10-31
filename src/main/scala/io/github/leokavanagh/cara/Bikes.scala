package io.github.leokavanagh.cara

import scala.math._
import requests._
import ujson._

object Bikes {

  case class Coord(latitude: Float, longitude: Float)

  val home = Coord(sys.env("HOMELAT").toFloat,
                   sys.env("HOMELONG").toFloat)

  // square roots are for losers
  def dist(c1: Coord, c2: Coord): Double = {
    math.pow(c2.latitude - c1.latitude, 2) + math.pow(c2.longitude - c1.longitude, 2)
    }

  val distFromHome = dist(_: Coord, home)

  // TODO: Read this properly - https://www.47deg.com/blog/scala-3-typeclasses/
  case class DBData(loc: Coord, available_bikes: Int, address: String)
  case class BleeperData(loc: Coord, frame_id: Int)

  def directionsToBike(myLoc: Coord, bikeLoc: Coord): String = {
    val base: String = "https://www.openstreetmap.org/directions?engine=graphhopper_foot&route="
    val url = s"""${base}
      |${myLoc.latitude}%2C${myLoc.longitude}%3B
      |${bikeLoc.latitude}%2C${bikeLoc.longitude}
      """.stripMargin.replace("\n", "").trim()
    url
  }

  def parseDublinBikes(dbResp: Response): Array[DBData] =
    (ujson.read(dbResp).arr.toArray
      .filter(_("status") == ujson.Value.JsonableString("OPEN"))
      .filter(_("available_bikes").num > 0)
      .map(x => DBData(
        Coord(x("latitude").str.toFloat, x("longitude").str.toFloat),
        x("available_bikes").num.toInt,
        x("address").str)))

  def parseBleeperBikes(bResp: Response): Array[BleeperData] = {
    val zero = ujson.Value.JsonableInt(0)
    (ujson.read(bResp).arr.toArray
      .filter(_("lock_state") == zero)
      .filter(_("alarm") == zero)
      .filter(_("visible") == zero)
      .filter(_("status") == zero)
      .filter(_("vehicle_type") == zero)
      .map(x => BleeperData(
        Coord(x("latitude").num.toFloat, x("longitude").num.toFloat),
        x("frame_id").num.toInt)))
  }


  def findBikes() = {
    val dublin: String = "https://data.smartdublin.ie/dublinbikes-api/last_snapshot"
    val bleeper: String = "https://data.smartdublin.ie/bleeperbike-api/last_snapshot"

    val bleeperResp = requests.get(bleeper)
    val dubResp = requests.get(dublin)

    val n = 3
    val bleeperBikes = parseBleeperBikes(bleeperResp).take(n).map(_.loc).map(directionsToBike(home, _))
    val dublinBikes = parseDublinBikes(dubResp).take(n).map(_.loc).map(directionsToBike(home, _))

    val bikes = (Array("Bikes:\n") ++ dublinBikes ++ bleeperBikes).mkString("\n * ")
    bikes
  }

}