package org.com.composeauthenticator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccount(
    val id: Int = 0,
    @PrimaryKey
    val sharedKey: String,
    val image: Int,
    val issuer: String?,
    val name: String?,
    val code: String?
)