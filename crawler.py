import json, hmac, hashlib, time, requests, base64, datetime, time
from requests.auth import AuthBase
import sys

granularity = int(sys.argv[1])

def getData(start, end):
    params = {'granularity': granularity,'start': start,'end': end}
    r = requests.get(api_url + 'products/BTC-USD/candles', auth=auth, params=params)
    return r.json()

# Create custom authentication for Exchange
class CoinbaseExchangeAuth(AuthBase):
    def __init__(self, api_key, secret_key, passphrase):
        self.api_key = api_key
        self.secret_key = secret_key
        self.passphrase = passphrase

    def __call__(self, request):
        timestamp = str(time.time())
        message = timestamp + request.method + request.path_url + (request.body or '')
        #print message
        hmac_key = base64.b64decode(self.secret_key)
        #print hmac_key
        signature = hmac.new(hmac_key, message, hashlib.sha256)
        signature_b64 = signature.digest().encode('base64').rstrip('\n')

        #print signature_b64

        request.headers.update({
            'CB-ACCESS-SIGN': signature_b64,
            'CB-ACCESS-TIMESTAMP': timestamp,
            'CB-ACCESS-KEY': self.api_key,
            'CB-ACCESS-PASSPHRASE': self.passphrase,
            'Content-Type': 'application/json'
        })
        return request

api_url = 'https://api.pro.coinbase.com/'
auth = CoinbaseExchangeAuth("1e85e9d76f1ce7bbfe9f598b583f551b", "l1MY2cRklE3XuHIOXTAiifOMJM7chvGoa2EO3laB03zMTSd/vHluuRhIVZTFZNiMSJTrKc0PJ2qQr0TAsbpcLg==", "LauncherDragon123!")



f = open("BTC-USD-" + str(granularity) + ".csv","w+")
d = 1531905113
d2 = datetime.datetime.fromtimestamp(d).strftime('%Y-%m-%dT%H:%M:%S+00:00')
d -= 200*granularity
d1 = datetime.datetime.fromtimestamp(d).strftime('%Y-%m-%dT%H:%M:%S+00:00')

while (d > 1504224000):
    print d

    json = getData(d1, d2)
    for item in json:
        ds = []
        i = 0
        for data in item:
            if (i == 0):
                ds.append(datetime.datetime.fromtimestamp(data).strftime('%Y-%m-%dT%H:%M:%S+00:00'))
                ds.append(str(data % 86400))
            else:
                ds.append(str(data))
            i += 1
        f.write(",".join(ds) + "\n")

    d -= 200*granularity
    d2 = d1
    d1 = datetime.datetime.fromtimestamp(d).strftime('%Y-%m-%dT%H:%M:%S+00:00')

    time.sleep(0.33)

d1 = datetime.datetime.fromtimestamp(1504224000).strftime('%Y-%m-%dT%H:%M:%S+00:00')
json = getData(d1, d2)

for item in json:
    ds = []
    i = 0
    for data in item:
        if (i == 0):
            ds.append(datetime.datetime.fromtimestamp(data).strftime('%Y-%m-%dT%H:%M:%S+00:00'))
            ds.append(str(data % 86400))
        else:
            ds.append(str(data))
        i += 1
    f.write(",".join(ds) + "\n")



f.close()


