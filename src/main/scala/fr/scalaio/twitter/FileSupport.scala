package fr.scalaio.twitter

import java.text.SimpleDateFormat
import java.util.Locale

import com.danielasfregola.twitter4s.http.serializers.CustomSerializers
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats}

trait FileSupport extends Json4sSupport {

  object TwitterDefaultFormats extends DefaultFormats {
    override def dateFormatter: SimpleDateFormat =
      new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.US)
  }

  val json4sFormats: Formats =
    TwitterDefaultFormats ++ CustomSerializers.all

  def toJson[T <: AnyRef](value: T): String =
    Serialization.write(value)(json4sFormats)

}
