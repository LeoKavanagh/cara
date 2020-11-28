
import cask._
import sys._
import io.github.leokavanagh.cara.Cara._


object Main extends cask.MainRoutes {

  val bot_token = sys.env("CARA_BOT_TOKEN")
  val chat_id = sys.env("TELEGRAM_CHAT_ID")
  val telegram_url: String = s"https://api.telegram.org/bot$bot_token"

  implicit val tel = Telegram(bot_token, chat_id)

  @cask.get("/")
  def hello() = {
    "Druid siar."
  }

  @cask.post("/receive")
  def receive(request: cask.Request) = {
    request.text
  }

  initialize()

}
