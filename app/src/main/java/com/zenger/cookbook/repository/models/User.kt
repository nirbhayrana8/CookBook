package com.zenger.cookbook.repository.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var uid: String = "",
                var name: String? = null,
                var email: String? = null,
                var photoUrl: String? = null,
                var phoneNumber: String? = null,
                var isAuthenticated: Boolean = false,
                var isNew: Boolean = false,
                var isCreated: Boolean = false) : Parcelable