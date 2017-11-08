package fr.scalaio.twitter

import scala.io.Source

object AnalyseMentionsMain extends FileSupport {

  import org.json4s._
  import org.json4s.native.JsonMethods._

  def main(args: Array[String]): Unit = {
    val lines: List[String] = Source.fromFile("path-to/twitter-mentions-scalaio.json").getLines().toList

//    displayCrazyMentionners(lines)
    displayCrazyTweets(lines)
  }

  def displayCrazyMentionners(lines: List[String]): Unit = {
    val result: List[(String, Int)] =
      lines
        .groupBy(getTweetUsername)
        .mapValues(_.size)
        .toList
        .sortBy(-_._2)

    println(s"distinct users: ${result.size}")
    result.foreach(r => println(s"${r._1},${r._2}"))
  }

  def displayCrazyTweets(lines: List[String]): Unit = {
    val result: List[TweetInteraction] =
      lines
        .map(getTweetInteraction)
        .sortBy(ti => -(ti.retweet_count + ti.favorite_count))

    result.foreach(r => println(s"${displayString(r.text)},${r.name},${r.screen_name},${r.created_at},${r.retweet_count},${r.favorite_count}"))
  }

  def displayString(s: String): String = '"' + s.replace("\n", "\\n").replace("\"", "\"\"") + '"'

  case class TweetInteraction(text: String, name: String, screen_name: String, created_at: String, retweet_count: BigInt, favorite_count: BigInt)

  def getTweetInteraction(line: String): TweetInteraction =
    (for {
      JObject(tweet) <- parse(line)
      JField("text", JString(text)) <- tweet
      JField("created_at", JString(created_at)) <- tweet
      JField("retweet_count", JInt(retweet_count)) <- tweet
      JField("favorite_count", JInt(favorite_count)) <- tweet
      JField("user", JObject(user)) <- tweet
      JField("name", JString(name)) <- user
      JField("screen_name", JString(screen_name)) <- user
    } yield TweetInteraction(text, name, screen_name, created_at, retweet_count, favorite_count)).head

  def getTweetUsername(line: String): String =
    (for {
      JObject(tweet) <- parse(line)
      JField("user", JObject(user)) <- tweet
      JField("name", JString(name)) <- user
      JField("screen_name", JString(screen_name)) <- user
    } yield s"$name ($screen_name)").head
}
