# User API Spec

## Register User
- Endpoint : POST /api/users

Request Body :
```json
{
  "username": "dwiraamadhan",
  "password": "12345",
  "name": "Dwi Ramadhan"
}
```

Response Body (Success) :
```json
{
  "data" : "Successful"
}
```

Response Body (Failed) :
```json
{
  "errors": "username must not blank" //for example 
}
```

## Login User
- Endpoint : POST /api/auth/login

Request Body :
```json
{
  "username": "dwiraamadhan",
  "password": "12345"
}
```

Response Body (Success) :
```json
{
  "data" : {
    "token": "TOKEN",
    "expiredAt": 12345 //miliseconds
  }
}
```

Response Body (Failed, 401) :
```json
{
  "errors": "username or password incorrect" 
}
```

## Update User
- Endpoint : PATCH /api/users/current

Request Header:
- X-API-TOKEN: Token(Mandatory)


Request Body :
```json
{
  "name": "dwiramadhannn", //put if only want to update name
  "password": "new password" //put if only want to password
}
```

Response Body (Success) :
```json
{
  "data" : {
    "name": "dwiramadhannn", //new data
    "password": "new password" //new data
  }
}
```

Response Body (Failed, 401) :
```json
{
  "errors": "unauthorized" 
}
```

## Get User
- Endpoint : GET /api/users/current

Request Header:
- X-API-TOKEN: Token(Mandatory)

Response Body (Success) :
```json
{
  "data" : {
    "username": "dwiraamadhan",
    "name": "Dwi Ramadhan"
  }
}
```

Response Body (Failed, 401) :
```json
{
  "errors": "unauthorized" 
}
```

## Logout User

- Endpoint : DELETE /api/auth/logout

Request Header:
- X-API-TOKEN: Token(Mandatory)

Response Body (Success) :
```json
{
  "data" : "Successful"
}
```