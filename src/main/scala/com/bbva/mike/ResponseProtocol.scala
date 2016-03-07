package com.bbva.mike

/**
  * Created by joseluisillanaruiz on 3/3/16.
  */
object ResponseProtocol {

  import spray.json._

  case class ResponseData(statusCode: String, statusMessage: String)



  /* json (un)marshalling */

  object ResponseData extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(ResponseData.apply)
  }

  /* implicit conversions */

  //implicit def sendMessageToTopic(sender: Sender): Sender = Sender(topicName = sender.topicName, messageValue = sender.messageValue)

  //implicit def sendResponseToCLient(responseMessage: Response): Response = Response(statusCode = responseMessage.statusCode, responseBody = responseMessage.responseBody)
}

