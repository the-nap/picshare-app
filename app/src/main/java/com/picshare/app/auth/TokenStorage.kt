@file:Suppress("DEPRECATION")

package com.picshare.app.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.openid.appauth.AuthState
import androidx.core.content.edit

object TokenStorage {

  private const val FILE_NAME = "picshare_auth_state"
  private const val KEY_AUTH_STATE = "auth_state_json"

  //TODO replace deprecated EncryptedSharedPreferences
  private fun getPreferences(context: Context) =
    EncryptedSharedPreferences.create(
      context,
      FILE_NAME,
      MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build(),
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

  fun save(context: Context, authState: AuthState){
    getPreferences(context).edit {
      putString(KEY_AUTH_STATE, authState.jsonSerializeString())
    }
  }

  fun load(context: Context): AuthState? {
    val json = getPreferences(context)
      .getString(KEY_AUTH_STATE, null) ?: return null
    return AuthState.jsonDeserialize(json)
  }

  fun clear(context: Context) {
    getPreferences(context).edit{
      remove(KEY_AUTH_STATE)
        .apply()
    }
  }
}