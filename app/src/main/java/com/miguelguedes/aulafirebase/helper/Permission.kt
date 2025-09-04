package com.miguelguedes.aulafirebase.helper

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission {

    companion object {
        fun requisitarPermissao(activity: Activity, permissoes: List<String>, requestCode: Int) {

            val permissoesNegadas =  mutableListOf<String>()
            permissoes.forEach { permissao ->
                val temPermissao = ContextCompat.checkSelfPermission(
                    activity, permissao
                ) == PackageManager.PERMISSION_GRANTED
                if (!temPermissao) permissoesNegadas.add(permissao)
            }

            permissoes.forEach { permissao ->
                val temPermissao = ContextCompat.checkSelfPermission(
                    activity, permissao
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (permissoesNegadas.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    activity,
                    permissoes.toTypedArray(),
                    requestCode
                )
            }
        }
    }

}