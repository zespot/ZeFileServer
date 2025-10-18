const http = require('http');
const versions_server = http.createServer( (request, response) => {
  response.end( JSON.stringify(process.versions) );
});
versions_server.listen(3000);

http.createServer(function (req, res) {
  res.writeHead(200, {'Content-Type': 'text/html'});
  res.end('Hello World!');
}).listen(3001);

const fs = require('fs');
const server = http.createServer((req, res) => {
    fs.readFile(__dirname + './', (err, data) => {
        if (err) {
            res.writeHead(404);
            res.end(JSON.stringify(err));
            return;
        }

        res.writeHead(200);
        res.end(data);
    })
});

server.listen(5000, () => {
    console.log("> server on 5000");
});

