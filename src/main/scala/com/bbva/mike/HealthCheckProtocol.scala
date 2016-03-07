package com.bbva.mike

/**
  * Created by joseluisillanaruiz on 3/3/16.
  */
object HealthCheckProtocol {

  import spray.json._

  case class HealthData(message: String, date: String)

  case object HealthDataResponseOK

  case object HealthDataResponseKO


  /* json (un)marshalling */

  object HealthData extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(HealthData.apply)
  }

  /* implicit conversions */

  //implicit def sendMessageToTopic(sender: Sender): Sender = Sender(topicName = sender.topicName, messageValue = sender.messageValue)

  //implicit def sendResponseToCLient(responseMessage: Response): Response = Response(statusCode = responseMessage.statusCode, responseBody = responseMessage.responseBody)
}

