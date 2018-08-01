package main

import java.io.{File, FileInputStream}
import java.net.URI
import java.util.Base64

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.http.client.methods.{HttpUriRequest, RequestBuilder}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.beans.BeanProperty

abstract class ApiCaller (credentialsPath: String, host: String) {

    val input = new FileInputStream(new File(credentialsPath))
    val yaml = new Yaml(new Constructor(classOf[Credentials]))
    val credentials = yaml.load(input).asInstanceOf[Credentials]

    var timestamp: String = ""

    def setTimestamp(): Unit = {
        timestamp = "%.2f".format(System.currentTimeMillis() / 1000.0)
    }

    def buildRequest(endpoint:String, scheme:String = "https", params: Map[String, String] = Map()): HttpUriRequest

    def getRestContent(request: HttpUriRequest): String = {
        val httpClient = HttpClients.createDefault


        val httpResponse = httpClient.execute(request)
        val entity = httpResponse.getEntity
        var content = ""
        if (entity != null) {
            val inputStream = entity.getContent
            content = io.Source.fromInputStream(inputStream).getLines.mkString
            inputStream.close
        }
        httpClient.getConnectionManager.shutdown
        content
    }

    def sign(secret: Array[Byte], message: String): Array[Byte] = {
        val sha256_HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = new SecretKeySpec(secret, "HmacSHA256")
        sha256_HMAC.init(secretKey)

        val hash = sha256_HMAC.doFinal(message.getBytes)

        hash
    }

}

class Credentials {
    @BeanProperty var key: String = ""
    @BeanProperty var secret: String = ""
    @BeanProperty var pass: String = ""
}
