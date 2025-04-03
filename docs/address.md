# Address API Spec

## Create Address

Endpoint : POST /api/contacts/{idContact}/addresses

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
  "street" : "JL. H. Marzuki",
  "city" : "Jakarta",
  "province" : "DKI Jakarta",
  "country" : "Indonesia",
  "postalCode" : "11530"
}
```

Response Body (Success) :
```json
{
  "data" : {
    "id" : "1",
    "street" : "JL. H. Marzuki",
    "city" : "Jakarta",
    "province" : "DKI Jakarta",
    "country" : "Indonesia",
    "postalCode" : "11530"
  } 
}
```

Response Body (Failed) :
```json
{
  "error" : "Contact is not found"
}
```

## Update Address
Endpoint : PUT /api/contacts/{idContact}/addresses/{idAddress}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
  "street" : "JL. H. Marzuki",
  "city" : "Jakarta",
  "province" : "DKI Jakarta",
  "country" : "Indonesia",
  "postalCode" : "11530"
}
```

Response Body (Success) :
```json
{
  "data" : {
    "id" : "1",
    "street" : "JL. H. Marzuki",
    "city" : "Jakarta",
    "province" : "DKI Jakarta",
    "country" : "Indonesia",
    "postalCode" : "11530"
  } 
}
```

Response Body (Failed) :
```json
{
  "error" : "Address is not found"
}
```
## Get Address
Endpoint : GET /api/contacts/{idContact}/adresses/{idAddress}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :
```json
{
  "data" : {
    "id" : "1",
    "street" : "JL. H. Marzuki",
    "city" : "Jakarta",
    "province" : "DKI Jakarta",
    "country" : "Indonesia",
    "postalCode" : "11530"
  } 
}
```

Response Body (Failed) :
```json
{
  "error" : "address is not found"
}
```

## Remove Address
Endpoint : DELETE /api/contacts/{idContact}/addresses/{idAddress}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :
```json
{
  "data" : "successful"
}
```

Response Body (Failed) :
```json
{
  "data" : "address is not found"
}
```

## List Address
Endpoint : GET /api/contacts/{idContact}/adresses

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :
```json
{
  "data" : [
    {
      "id" : "1",
      "street" : "JL. H. Marzuki",
      "city" : "Jakarta",
      "province" : "DKI Jakarta",
      "country" : "Indonesia",
      "postalCode" : "11530"
    },
    {
      "id" : "2",
      "street" : "JL. Perumahan Kebon Jeruk Baru",
      "city" : "Jakarta",
      "province" : "DKI Jakarta",
      "country" : "Indonesia",
      "postalCode" : "11530"
    }
  ]
}
```

Response Body (Failed) :
```json
{
  "error" : "address is not found"
}
```