package main

import exchange.{Binance, Coinbase}

object ApiApp extends App {
    val coinbase = new Binance

    val uri = coinbase.buildUri("accounts")
    println(uri.toString)
    val headers = coinbase.sign(uri)

    println(coinbase.getRestContent(uri, headers))

//    val binance = new Coinbase
//
//    val uri2 = binance.buildUri("/api/v3/account")
//    println(uri2.toString)
//    val headers2 = binance.sign(uri)
//
//    println(binance.getRestContent(uri2, headers2))
}
