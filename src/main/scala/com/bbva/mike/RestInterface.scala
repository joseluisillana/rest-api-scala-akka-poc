package com.bbva.mike

import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor._
import com.bbva.mike.KafkaProducerProtocol.StructuredLog
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
                  /*entity(as[Sender]) { sender => requestContext =>
                  val responder = createResponder(requestContext)
                  sendMessageToKafka(sender) match {
                    case true => sender ! SenderResponseOK
                    case _ => sender ! SenderResponseKO
                  }
                }*/
                  //complete(Sender("eltopic", "elmensaje"))
                  entity(as[StructuredLog]) { structuredLog =>
                    complete(Sender(topicName, structuredLog.messageValue))
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


      private def createResponder(requestContext: RequestContext) =
      {
        context.actorOf(Props(new Responder(requestContext)))
      }

      private def sendMessageToKafka(sender: Sender): Boolean =
      {
        val result = true
        result
      }

      private def getCurrentHour: String =
      {
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
        requestContext.complete(StatusCodes.BadGateway)
        killYourself
    }

    private def killYourself = self ! PoisonPill

  }

