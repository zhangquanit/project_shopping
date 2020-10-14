package com.snqu.shopping.data.user.entity

data class InvitedEntity(val code:String, var invited:String, val is_new:String,val canSkipInvited:Int){
    override fun toString(): String {
        return "InvitedEntity(code='$code', invited='$invited', is_new='$is_new')"
    }
}