package main

import exchange.{Binance, Coinbase}

object ApiApp extends App {

    val coinbase = new Coinbase("src/main/config/coinbase_credentials.yml", "api.pro.coinbase.com")

    println(coinbase.getRestContent(coinbase.buildRequest("accounts")))


    val binance = new Binance("src/main/config/binance_credentials.yml", "api.binance.com")

    println(binance.getRestContent(binance.buildRequest("/api/v3/account")))

}
