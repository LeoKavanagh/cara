package io.github.leokavanagh.main

//import sys._
//import cask._
//import ujson._
import io.github.leokavanagh.cara.Cara._

object Main extends cask.MainRoutes {

  val bot_token: String = sys.env("CARA_BOT_TOKEN")
  val chat_id: Int = sys.env("TELEGRAM_CHAT_ID").toInt
  val base_url: String = s"https://api.telegram.org/bot"

  implicit val tel: Telegram = Telegram(base_url, bot_token, chat_id)

  // heroku dynamically assigns a port
  // https://stackoverflow.com/questions/15693192/
  override def port: Int = scala.util.Properties.envOrElse("PORT", "8080").toInt
  override def host: String = "0.0.0.0"

  @cask.get(path="/")
  def hello(): String = {
    "Druid siar."
  }

  // For when I send a message to the bot in Telegram
  @cask.post(path="/receive")
  def receive_from_telegram(request: cask.Request): Int = {
    val parsed_message = receive_message(request.text)
    val response: Int = process_message(parsed_message.text)
    response
  }

  // For when I want to make the bot send a message
  // (as opposed to respond to one) using curl or whatever
  @cask.postJson(path="/msg")
  def msg(text: String)(implicit tel: Telegram): Int = {
    println("in method msg")
    println(text)
    send_text(text)
  }

  // requests.post(host + "/pj1", data=ujson.Obj("message_text" -> "foo"))
  // curl -X POST localhost:8080/pj1 -d '{"message_text": "asdf"}'
  // Doesn't handle extra stuff turning up
  @cask.postJson(path="/pj1")
  def pj1(message_text: String): String = {
    message_text
  }

  // curl -X POST localhost:8080/pj1 -d '{"message_text": "asdf"}'
  // requests.post(host + "/p1", data=Map("message_text" -> "foo"))
  @cask.post(path="p1")
  def p1(request: cask.Request): String = {
    request.text
  }

  initialize()

}
