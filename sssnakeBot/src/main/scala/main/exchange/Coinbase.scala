package main.exchange

import main.{ApiCaller, Credentials}
import org.apache.http.client.methods.{HttpUriRequest, RequestBuilder}
import org.apache.http.client.utils.URIBuilder
import java.util.Base64

class Coinbase(credentialsPath: String, host: String) extends ApiCaller (credentialsPath, host) {

    override def buildRequest(endpoint: String, scheme: String, params: Map[String, String]): HttpUriRequest = {
        setTimestamp()

        var uriBuilder = new URIBuilder()
            .setScheme(scheme)
            .setHost(host)
            .setPath(endpoint)
        params.map(m => uriBuilder.addParameter(m._1,m._2))

        val uri = uriBuilder.build()

        val message = timestamp + "GET" + uri.getPath

        val hash = Base64.getEncoder.encodeToString(sign(Base64.getDecoder.decode(credentials.secret.getBytes), message))

        val requestBuilder = RequestBuilder.get()
            .setUri(uri)

        makeHeaders(hash, credentials).map(h => requestBuilder.setHeader(h._1, h._2))

        requestBuilder.build
    }

    def makeHeaders(hash: String, credentials: Credentials): Map[String, String] = {
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
