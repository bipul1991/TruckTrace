package com.example.bipul.truckapp.model

class UserModel
{
    var userName : String? = null
    var userId : String?= null
    var userAdrs : String?= null
    var userLat: String?=null
    var userLun:String?=null

constructor(){}

    constructor(userName : String?,userId : String?,userAdrs : String?,userLat: String?,userLun:String? )
    {
        this.userName = userName
        this.userId = userId
        this.userAdrs= userAdrs
        this.userLat= userLat
        this.userLun= userLun
    }



}