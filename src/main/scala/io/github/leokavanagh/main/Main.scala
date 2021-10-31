package io.github.leokavanagh.main

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
    "Tá tú san áit micheart chun úsáid a bhaint asam."
  }

  // suggested practice: Let the webhook url be the secret bot token
  // so that others can't guess it and control the bot
  @cask.route(path=s"/${bot_token}", methods=Seq("get", "post"))
  def receive_from_telegram(request: cask.Request): Int = {
    if (request.exchange.getRequestMethod.equalToString("post")) {
	  val parsed_message = receive_text(request.text)
      val response: Int = process_message(parsed_message.text)
      response
	}
    else {
      200 // maybe this is confusing?
	}
  }

  // For when I want to make the bot send a message
  // (as opposed to respond to one) using curl or whatever
  @cask.postJson(path=s"/msg${bot_token}")
  def msg(text: String)(implicit tel: Telegram): Int = {
    println("in method msg")
    println(text)
    send_text(text)
  }

  initialize()

}
