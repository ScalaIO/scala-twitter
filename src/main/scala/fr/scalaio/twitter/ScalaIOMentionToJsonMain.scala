package fr.scalaio.twitter

import java.io.PrintWriter
import java.time.{LocalDateTime, Month, ZoneId}
import java.util.concurrent.TimeUnit

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.Await

object ScalaIOMentionToJsonMain extends FileSupport {

  val limitDate: LocalDateTime =
    LocalDateTime.of(2017, Month.NOVEMBER, 1, 0, 0)

  val filename = "~/tweets.json"

  def main(args: Array[String]): Unit = {
    /*
    To use this program, you have to define those environment variables

export TWITTER_CONSUMER_TOKEN_KEY='my-consumer-key'
export TWITTER_CONSUMER_TOKEN_SECRET='my-consumer-secret'
export TWITTER_ACCESS_TOKEN_KEY='my-access-key'
export TWITTER_ACCESS_TOKEN_SECRET='my-access-secret'
     */

    val restClient: TwitterRestClient = TwitterRestClient()

    val tweets = getMentions(restClient)

    val printer = new PrintWriter(filename)

    println(s"write to $filename")
    tweets.foreach { tweet =>
      println(s"write ${tweet.id}")
      printer.println(toJson(tweet))
    }

    printer.close()

  }

  def getMentions(restClient: TwitterRestClient,
                  max_id: Option[Long] = None): Seq[Tweet] = {
    println(s"get mentions from $max_id")

    val ratedData: RatedData[Seq[Tweet]] =
      Await.result(restClient.mentionsTimeline(count = 200,
                                               max_id = max_id,
                                               contributor_details = true),
                   Duration(5, TimeUnit.SECONDS))

    println(ratedData.rate_limit)
    val tweets = ratedData.data

    if (!tweets.exists(
          tweet =>
            limitDate.isAfter(tweet.created_at.toInstant
              .atZone(ZoneId.systemDefault())
              .toLocalDateTime)))
      tweets ++ getMentions(restClient, Some(tweets.last.id))
    else
      tweets
  }

}
