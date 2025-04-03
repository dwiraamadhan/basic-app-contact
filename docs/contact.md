# Contact API Spec

## Create Contact

Endpoint : POST /api/contacts

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
  "firstname" : "dwi",
  "lastname" : "ramadhan",
  "email" : "dwiramadhan@gmail.com",
  "phone" : "081111111"
}
```

Response Body(Success) :
```json
{
  "data" : {
    "id" : "1",
    "firstname" : "dwi",
    "lastname" : "ramadhan",
    "email" : "dwiramadhan@gmail.com",
    "phone" : "081111111"
  }
}
```

Response Body(Failed) :
```json
{
  "error" : "email format invalid" //for example
}
```

## Update Contact
Endpoint : PUT /api/contacts/{idContact}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
  "firstname" : "dwi",
  "lastname" : "ramadhan",
  "email" : "dwiramadhan@gmail.com",
  "phone" : "081111111"
}
```

Response Body(Success) :
```json
{
  "data" : {
    "id" : "1",
    "firstname" : "dwi",
    "lastname" : "ramadhan",
    "email" : "dwiramadhan@gmail.com",
    "phone" : "081111111"
  }
}
```

Response Body(Failed) :
```json
{
  "error" : "email format invalid" //for example
}
```


## Get Contact
Endpoint : GET /api/contacts/{idContact}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body(Success) :
```json
{
  "data" : {
    "id" : "1",
    "firstname" : "dwi",
    "lastname" : "ramadhan",
    "email" : "dwiramadhan@gmail.com",
    "phone" : "081111111"
  }
}
```

Response Body(Failed, 404) :
```json
{
  "error" : "contact is not found"
}
```


## Search Contact
Endpoint : GET /api/contacts

Query Param:
- name : String, contact firstname or lastname, using like query, optional
- phone : String, contact phone, using like query, optional
- email : String, contact email, using like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body(Success) :
```json
{
  "data" : [
    {
      "id" : "1",
      "firstname" : "dwi",
      "lastname" : "ramadhan",
      "email" : "dwiramadhan@gmail.com",
      "phone" : "081111111"
    },
    {
      "id" : "2",
      "firstname" : "jessie",
      "lastname" : "ramadhan",
      "email" : "jessieramadhan@gmail.com",
      "phone" : "08222222"
    }
  ],
  "paging" : {
    "currentPage" : 0,
    "totalPage" : 10,
    "size" : 10
  }
}
```

Response Body(Failed) :
```json
{
  "data" : "unauthorized"
}
```


## Remove Contact
Endpoint : DELETE /api/contactcs/{idContact}

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body(Success) :
```json
{
  "data" : "successful"
}
```

Response Body(Failed, 404) :
```json
{
  "error" : "contact is not found"
}
```