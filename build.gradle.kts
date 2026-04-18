plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply  false// This replaces the old composeOptions block
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false   // ← أضف هذا

}