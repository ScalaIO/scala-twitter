package fr.scalaio.twitter

import java.text.SimpleDateFormat
import java.util.Locale

import com.danielasfregola.twitter4s.http.serializers._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats}

trait FileSupport extends Json4sSupport {

  implicit object TwitterDefaultFormats extends DefaultFormats {
    override def dateFormatter: SimpleDateFormat =
      new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.US)
  }

  val json4sFormats: Formats =
    TwitterDefaultFormats ++ List(LocalDateSerializer, DisconnectionCodeSerializer)

  def toJson[T <: AnyRef](value: T): String =
    Serialization.write(value)

  def fromJson[T : Manifest](json: String): T =
    Serialization.read[T](json)

}
