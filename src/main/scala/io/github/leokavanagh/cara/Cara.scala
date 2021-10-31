package io.github.leokavanagh.cara

import io.github.leokavanagh.cara.Bikes.findBikes

object Cara {

  // the bot and chat ID shouldn't really be linked but I'm the only user so whatever
  case class Telegram(base_url: String, bot_token: String, chat_id: Int)
  case class ParsedMessage(text: String, chat_id: String)

  
  def send_text(message_text: String)(implicit telegram: Telegram): Int = {
    val telegram_url: String = s"${telegram.base_url}${telegram.bot_token}"

    // used to be Map[String, String] but chat_id is numeric.
    // Maybe it's better to treat it as an int?
    val msg_data: Map[String, String] = Map(
      "text" -> message_text,
      "chat_id" -> telegram.chat_id.toString
    )
    val url: String = telegram_url + "/sendMessage"
    val response = requests.post(url, data=msg_data)
    response.statusCode
  }

  def receive_text(request_text: String): ParsedMessage = {
    val uj_msg = ujson.read(request_text)("message")
    // only doing this because I know what fields I want

    val msg_map: Map[String, String] = Map(
      "text" -> uj_msg("text").value.toString,
      "id" -> uj_msg("chat")("id").toString
    )
    val parsed_msg = ParsedMessage(msg_map("text"), msg_map("id"))
    parsed_msg
  }


  def process_message(msg: String)(implicit telegram: Telegram): Int = {
    val first_word = msg.split(" ")(0).toLowerCase()

    first_word match {
      case "wake" => send_text("What do you want?")
      case "weather" => send_text("call dublin-forecast")
      case "bikes" => send_text(findBikes())  // TODO: ask for location
      case _ => send_text(s"I can't do ${first_word} yet. This is if-statement AI")
    }
  }

}

