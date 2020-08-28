package com.zenger.cookbook.repository

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var uid: String = "",
                var name: String = "",
                var email: String = "",
                var isAuthenticated: Boolean = false,
                var isNew: Boolean = true,
                var isCreated: Boolean = false) : Parcelable {

}