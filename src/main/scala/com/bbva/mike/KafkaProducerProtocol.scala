package com.bbva.mike

/**
  * Created by joseluisillanaruiz on 3/3/16.
  */
object KafkaProducerProtocol {

  import spray.json._

  case class Sender(topicName: String, messageValue: String)

  case object SenderResponseOK

  case object SenderResponseKO


  /* json (un)marshalling */

  object Sender extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(Sender.apply)
  }

  /* implicit conversions */

  //implicit def sendMessageToTopic(sender: Sender): Sender = Sender(topicName = sender.topicName, messageValue = sender.messageValue)

  //implicit def sendResponseToCLient(responseMessage: Response): Response =
  //  Response(statusCode = responseMessage.statusCode, responseBody = responseMessage.responseBody)
}

