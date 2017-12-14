# nong-taphan

A simple GeoSPARQL server

## Usage

### Run server
````
lein ring server-headless
````

### Example
````
curl -v -X POST localhost:3000/query -d @examples/ex1.sparql
````


