package main

import exchange.{Binance, Coinbase}

object ApiApp extends App {
    val coinbase = new Coinbase("src/main/config/coinbase_credentials.yml", "api.pro.coinbase.com")

    println(coinbase.getRestContent(coinbase.buildRequest("accounts")))

//    val binance = new Coinbase
//
//    val uri2 = binance.buildUri("/api/v3/account")
//    println(uri2.toString)
//    val headers2 = binance.sign(uri)
//
//    println(binance.getRestContent(uri2, headers2))

    val binance = new Binance("src/main/config/binance_credentials.yml", "api.binance.com")

    println(binance.getRestContent(binance.buildRequest("/api/v3/account")))
}
