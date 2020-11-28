package io.github.leokavanagh.cara

import sys._
import requests._


object Cara {

  // the bot and chat ID shouldn't really be linked but I'm the only user so whatever
  case class Telegram(bot_token: String, chat_id: String)
  case class ReceivedMessage(text: String, chat_id: String)

  def send_text(message_text: String)(implicit telegram: Telegram): Int = {
    val bot_token = telegram.bot_token
    val telegram_url: String = s"https://api.telegram.org/bot$bot_token"
    val msg_data: Map[String, String] = Map(
      "text" -> message_text,
      "chat_id" -> telegram.chat_id
    )
    val url: String = telegram_url + "/sendMessage"
    val response = requests.post(url, data=msg_data)
    response.statusCode
  }


  // TODO: Types and all that
  def receive_message(raw_msg: Map[String, Map[String, String]]): ReceivedMessage = {
    val msg_content: Map[String, String] = raw_msg("message")
    val parsed_msg = ReceivedMessage(msg_content("text"), msg_content("id"))
    parsed_msg
  }

  def process_message(msg: String) = {
    val first_word = msg.split(" ")(0).toLowerCase()

    first_word match {
      case "weather" => println("call dublin-forecast")
      case "read" => println("call article-reader")
      case "remind" => Thread.sleep(1000); println(msg)
      case _ => println("I can't do that yet. This is if-statement AI")
    }
  }

}

