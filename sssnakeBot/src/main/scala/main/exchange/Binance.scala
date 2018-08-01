package main.exchange

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Base64

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import main.{ApiCaller, Credentials}
import org.apache.commons.codec.binary.Hex
import org.apache.http.client.methods.{HttpUriRequest, RequestBuilder}
import org.apache.http.client.utils.URIBuilder

class Binance(credentialsPath: String, host: String) extends ApiCaller (credentialsPath, host) {

    override def setTimestamp(): Unit = {
        timestamp = "%d".format(System.currentTimeMillis())
    }

    override def buildRequest(endpoint: String, scheme: String, params: Map[String, String]): HttpUriRequest = {
        setTimestamp()

        var uriBuilder = new URIBuilder()
            .setScheme(scheme)
            .setHost(host)
            .setPath(endpoint)
        params.map(m => uriBuilder.addParameter(m._1,m._2))

        uriBuilder.addParameter("timestamp", timestamp)

        var uri = uriBuilder.build()

        val message = uri.getQuery

        val hash = new String(Hex.encodeHex(sign(credentials.secret.getBytes, message)))

        uriBuilder.addParameter("signature", hash)
        uri = uriBuilder.build()

        println(uri)

        val requestBuilder = RequestBuilder.get()
            .setUri(uri)

        makeHeaders(credentials).map(h => requestBuilder.setHeader(h._1, h._2))

        requestBuilder.build
    }

    def makeHeaders(credentials: Credentials): Map[String, String] = {
        var headers = Map(
            "X-MBX-APIKEY" -> credentials.key,
            "Content-Type" -> "application/json"
        )
        headers
    }

}
