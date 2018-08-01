package main

import java.io.{File, FileInputStream}
import java.net.URI
import java.util.Base64

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.beans.BeanProperty

abstract class ApiCaller (credentialsPath: String, host: String) {

    val input = new FileInputStream(new File(credentialsPath))
    val yaml = new Yaml(new Constructor(classOf[Credentials]))
    val credentials = yaml.load(input).asInstanceOf[Credentials]


    def buildUri(endpoint:String, scheme:String = "https", params: Map[String, String] = Map()): URI = {
        var uri = new URIBuilder()
            .setScheme(scheme)
            .setHost(host)
            .setPath(endpoint)
        params.map(m => uri.addParameter(m._1,m._2))

        println(host)
        println(uri.getPath)
        uri.build()
    }

    def getRestContent(uri: URI, headers: Map[String, String] = Map()): String = {
        val httpClient = HttpClientBuilder.create().build()
        val request = RequestBuilder.get().setUri(uri)

        headers.map(h => request.setHeader(h._1, h._2))

        val httpResponse = httpClient.execute(request.build)
        val entity = httpResponse.getEntity
        var content = ""
        if (entity != null) {
            val inputStream = entity.getContent
            content = io.Source.fromInputStream(inputStream).getLines.mkString
            inputStream.close()
        }
        httpClient.close()
        content
    }

    def sign(uri: URI): Map[String, String] = {
        val timestamp = "%.2f".format(System.currentTimeMillis() / 1000.0)

        val message = timestamp + "GET" + uri.getPath
        println(message)

        val sha256_HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = new SecretKeySpec(Base64.getDecoder.decode(credentials.secret), "HmacSHA256")
        sha256_HMAC.init(secretKey)

        val hash = Base64.getEncoder.encodeToString(sha256_HMAC.doFinal(message.getBytes()))

        println(hash)

        var headers = Map(
            "CB-ACCESS-SIGN" -> hash,
            "CB-ACCESS-TIMESTAMP" -> timestamp.toString,
            "CB-ACCESS-KEY" -> credentials.key,
            "CB-ACCESS-PASSPHRASE" -> credentials.pass,
            "Content-Type" -> "application/json"
        )
        headers
    }

}

class Credentials {
    @BeanProperty var key: String = ""
    @BeanProperty var secret: String = ""
    @BeanProperty var pass: String = ""
}
