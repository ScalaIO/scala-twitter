package fr.scalaio.twitter

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities._

import scala.concurrent.ExecutionContext.Implicits._

object ScalaIOMentionToCSVMain {

  def main(args: Array[String]): Unit = {
    // for programmatic parameter passing
//    val consumerToken = ConsumerToken(key = "key", secret = "secret")
//    val accessToken = AccessToken(key = "key", secret = "secret")
//    val restClient = TwitterRestClient(consumerToken, accessToken)
    // instead of...
    val restClient: TwitterRestClient = TwitterRestClient()

    val tweets = restClient.mentionsTimeline(include_entities = false)
    for (result: RatedData[Seq[Tweet]] <- tweets) {
      println(s"remaining: ${result.rate_limit.remaining} / ${result.rate_limit.limit} (reset = ${result.rate_limit.reset})")
      for {
        tweet: Tweet <- result.data
        if displayableTweet(tweet)
      } {
        println(displayCsvOf(tweet))
      }
    }
  }

  def displayCsvOf(tweet: Tweet) = {
    def escapeText(s: String) = "\"" + s.replace("\"", "\"\"").replace("\n", "\\n") + "\""

    val user_info = s"""${tweet.user.map(_.id_str).getOrElse("")},${escapeText(tweet.user.map(_.screen_name).getOrElse(""))},${tweet.user.map(_.name).getOrElse("")}"""

    s"""${tweet.id_str},${tweet.created_at},$user_info,${escapeText(tweet.text)},${tweet.retweet_count},${tweet.favorite_count},${tweet.lang.getOrElse("")}"""
  }

  def displayableTweet(tweet: Tweet) =
    tweet.retweeted_status.isEmpty && tweet.quoted_status.isEmpty && tweet.user.isDefined

}
