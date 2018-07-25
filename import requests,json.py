import requests,json
url = "https://www.googleapis.com/qpxExpress/v1/trips/search?key=mykeyhere"
my_json_data = json.load(open("request.json"))
req = requests.post(url,data=my_json_data)
print req.text
print 
print req.json