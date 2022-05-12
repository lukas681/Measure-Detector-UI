import http.client
import mimetypes
from codecs import encode

conn = http.client.HTTPSConnection("localhost", 8080)
dataList = []
boundary = 'wL36Yn8afVp8Ag7AmP8qZ0SA4n1v9T'
dataList.append(encode('--' + boundary))
dataList.append(encode('Content-Disposition: form-data; name=image; filename={0}'.format('/C:/Users/Lukas/Documents/Github/MeasureDetector/MeasureDetector/demo/IMSLP454435-PMLP738602-Il_tempio_d_amore_Sinfonia-0011.jpg')))

fileType = mimetypes.guess_type('/C:/Users/Lukas/Documents/Github/MeasureDetector/MeasureDetector/demo/IMSLP454435-PMLP738602-Il_tempio_d_amore_Sinfonia-0011.jpg')[0] or 'application/octet-stream'
dataList.append(encode('Content-Type: {}'.format(fileType)))
dataList.append(encode(''))

with open('/C:/Users/Lukas/Documents/Github/MeasureDetector/MeasureDetector/demo/IMSLP454435-PMLP738602-Il_tempio_d_amore_Sinfonia-0011.jpg', 'rb') as f:
  dataList.append(f.read())
dataList.append(encode('--' + boundary))
dataList.append(encode('Content-Disposition: form-data; name=Test;'))

dataList.append(encode('Content-Type: {}'.format('text/plain')))
dataList.append(encode(''))

dataList.append(encode("Second"))
dataList.append(encode('--'+boundary+'--'))
dataList.append(encode(''))
body = b'\r\n'.join(dataList)
payload = body
headers = {
   'Content-type': 'multipart/form-data; boundary={}'.format(boundary) 
}
conn.request("POST", "/upload", payload, headers)
res = conn.getresponse()
data = res.read()
print(data.decode("utf-8"))