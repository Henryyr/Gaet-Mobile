package com.example.gaetdriver.core.base.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLocale

/**
 * Hand-rolled Type-Safe Localization (Stable alternative to Lyricist).
 */
interface AppStrings {
    val appName: String
    val welcomeBack: String
    val registerHere: String
    val signInToContinue: String
    val signUpToContinue: String
    val email: String
    val password: String
    val firstName: String
    val lastName: String
    val phoneNumber: String
    val login: String
    val signUp: String
    val dontHaveAccount: String
    val alreadyHaveAccount: String
    val home: String
    val activity: String
    val library: String
    val profile: String
    val add: String
    val logout: String
    val search: String
    val settings: String
    val homeDescription: String
    val noActivity: String
    val noActivityDescription: String
    val libraryEmpty: String
    val libraryDescription: String
    val profileSettings: String
    val themePreference: String
    val systemDefault: String
    val lightMode: String
    val darkMode: String
    val oops: String
    val retry: String
    val chooseOption: String
    val takePhoto: String
    val chooseGallery: String
}

val EnStrings = object : AppStrings {
    override val appName = "LifeOS"
    override val welcomeBack = "Welcome Back"
    override val registerHere = "Register Here"
    override val signInToContinue = "Sign in to continue"
    override val signUpToContinue = "Sign up to continue"
    override val email = "Email"
    override val password = "Password"
    override val firstName = "First Name"
    override val lastName = "Last Name"
    override val phoneNumber = "Phone Number"
    override val login = "Login"
    override val signUp = "Sign Up"
    override val dontHaveAccount = "Don't have an account? Sign Up"
    override val alreadyHaveAccount = "Already have an account? Login"
    override val home = "Home"
    override val activity = "Activity"
    override val library = "Library"
    override val profile = "Profile"
    override val add = "Add"
    override val logout = "Logout"
    override val search = "Search"
    override val settings = "Settings"
    override val homeDescription = "Home and Display Catalog is here."
    override val noActivity = "No Activity Yet"
    override val noActivityDescription = "Your upload history and logs will appear here."
    override val libraryEmpty = "Library is Empty"
    override val libraryDescription = "Manage all your data, filter, and search from here."
    override val profileSettings = "Profile Settings"
    override val themePreference = "Theme Preference"
    override val systemDefault = "System Default"
    override val lightMode = "Light Mode"
    override val darkMode = "Dark Mode"
    override val oops = "Oops!"
    override val retry = "Retry"
    override val chooseOption = "Choose Option"
    override val takePhoto = "Take a Photo"
    override val chooseGallery = "Choose from Gallery"
}

val IdStrings = object : AppStrings {
    override val appName = "LifeOS"
    override val welcomeBack = "Selamat Datang Kembali"
    override val registerHere = "Daftar Di Sini"
    override val signInToContinue = "Masuk untuk melanjutkan"
    override val signUpToContinue = "Daftar untuk melanjutkan"
    override val email = "Email"
    override val password = "Kata Sandi"
    override val firstName = "Nama Depan"
    override val lastName = "Nama Belakang"
    override val phoneNumber = "Nomor Telepon"
    override val login = "Masuk"
    override val signUp = "Daftar"
    override val dontHaveAccount = "Belum punya akun? Daftar"
    override val alreadyHaveAccount = "Sudah punya akun? Masuk"
    override val home = "Beranda"
    override val activity = "Aktivitas"
    override val library = "Koleksi"
    override val profile = "Profil"
    override val add = "Tambah"
    override val logout = "Keluar"
    override val search = "Cari"
    override val settings = "Pengaturan"
    override val homeDescription = "Beranda dan Katalog Tampilan ada di sini."
    override val noActivity = "Belum Ada Aktivitas"
    override val noActivityDescription = "Riwayat unggahan dan log Anda akan muncul di sini."
    override val libraryEmpty = "Perpustakaan Kosong"
    override val libraryDescription = "Kelola semua data Anda, filter, dan cari dari sini."
    override val profileSettings = "Pengaturan Profil"
    override val themePreference = "Preferensi Tema"
    override val systemDefault = "Default Sistem"
    override val lightMode = "Mode Terang"
    override val darkMode = "Mode Gelap"
    override val oops = "Ups!"
    override val retry = "Coba Lagi"
    override val chooseOption = "Pilih Opsi"
    override val takePhoto = "Ambil Foto"
    override val chooseGallery = "Pilih dari Galeri"
}

val LocalStrings = staticCompositionLocalOf { EnStrings }

/**
 * Returns the strings based on current system locale.
 */
@Composable
fun rememberStrings(): AppStrings {
    val locale = LocalLocale.current.platformLocale.language
    return if (locale == "in" || locale == "id") IdStrings else EnStrings
}
