package com.bbva.mike

import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor._
import com.bbva.mike.KafkaProducerProtocol.{Sender, StructuredLog}
import spray.http.StatusCodes

import spray.routing._

import scala.language.postfixOps

/**
  * Created by joseluisillanaruiz on 3/3/16.
  */
class RestInterface extends HttpServiceActor
  with RestApi {

  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging {
  actor: Actor =>

  import com.bbva.mike.HealthCheckProtocol._
  import com.bbva.mike.KafkaProducerProtocol._
  import com.bbva.mike.ResponseProtocol._
  import spray.http.MediaTypes
  import spray.httpx.SprayJsonSupport._

  def routes: Route =

    pathPrefix("mikeApi") {
      pathPrefix("_healthcheck") {
        pathEnd {
          respondWithMediaType(MediaTypes.`application/json`) {
            get {
              complete(HealthData("MIKE API STATUS", getCurrentHour))
            }
          }
        }
      } ~
        pathPrefix("log") {
          pathPrefix("structured" / Segment) { (topicName: String) => {
            pathEnd {
              respondWithMediaType(MediaTypes.`application/json`) {
                post {
                  entity(as[StructuredLog]) { structuredLog => requestContext =>
                    val kafkaWriter = createKafkaWriter(Sender(topicName, structuredLog.messageValue))
                    kafkaWriter ! SendToKafka
                    requestContext.complete(ResponseData(StatusCodes.NoContent.toString(), "MIKE - Received "))
                  }
                }
              }
            }
          }
          }

          /*
          System.out.println(topic)
          requestContext.complete(StatusCodes.BadGateway)
          */
        }
    }


  private def createResponder(requestContext: RequestContext) = {
    context.actorOf(Props(new Responder(requestContext)))
  }

  private def createKafkaWriter(sender: Sender) = {
    context.actorOf(Props(new KafkaWriter(sender)))
  }


  private def getCurrentHour: String = {
    val today = Calendar.getInstance().getTime()
    val dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss")
    return dateFormat.format(today)
  }


}

class Responder(requestContext: RequestContext) extends Actor with ActorLogging {

  import com.bbva.mike.KafkaProducerProtocol._

  def receive = {

    case SenderResponseOK =>
      requestContext.complete(StatusCodes.NoContent)
      killYourself
    case SenderResponseKO =>
      requestContext.complete(StatusCodes.InternalServerError)
      killYourself
  }

  private def killYourself = self ! PoisonPill

}

class KafkaWriter(sender: Sender) extends Actor with ActorLogging {

  import com.bbva.mike.KafkaProducerProtocol._

  def receive = {

    case SendToKafka =>
      // Do something
      sendMessageToKafka(sender) match {
        case true => self ! KillActor
        case _ => self ! KillActor
      }
    case KillActor =>
      killYourself
  }

  private def killYourself = self ! PoisonPill

  private def sendMessageToKafka(sender: Sender): Boolean = {
    val result = true
    val max = 10000L
    var i = 0
    while (i < max) {
      System.out.println(s"MIKE PET NUM ${i} of ${max} data ingested: ${sender.topicName} and the messege is ${sender.messageValue}")
      i = i + 1
    }
    result
  }

}

