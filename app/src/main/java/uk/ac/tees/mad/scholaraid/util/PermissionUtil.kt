package uk.ac.tees.mad.scholaraid.util

import android.Manifest
import android.os.Build

object PermissionUtil {
    val cameraPermissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    } else {
        listOf(Manifest.permission.CAMERA)
    }

    val galleryPermissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    } else {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            emptyList() // No permissions needed for Android 11+ for gallery access
        }
    }
}