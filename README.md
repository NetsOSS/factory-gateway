Adding a person:

    http -v -f POST http://localhost:8080/persons name=trygve

Listing all persons

    http -v http://localhost:9090/persons

Searching for a person based on name:

    http -v GET "http://localhost:8080/search?name=Iv"
