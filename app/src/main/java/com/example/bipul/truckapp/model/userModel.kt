package com.example.bipul.truckapp.model

class UserModel
{
    var userName : String? = null
    var userId : String?= null
    var transPname : String?= null
    var userAdrs : String?= null
    var userLat: String?=null
    var userLun:String?=null
    var transPortId:String?=null

constructor(){}

    constructor(userName : String?,userId : String?,transPname : String?,userAdrs :
                            String?,userLat: String?,userLun:String?,transPortId:String? )
    {
        this.userName = userName
        this.userId = userId
        this.transPname =transPname
        this.userAdrs= userAdrs
        this.userLat= userLat
        this.userLun= userLun
        this.transPortId=transPortId

    }



}