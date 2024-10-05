package com.dicoding.geotaggingjbg.ui.login

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.geotaggingjbg.R
import com.dicoding.geotaggingjbg.data.pref.UserModel
import com.dicoding.geotaggingjbg.databinding.ActivityLoginBinding
import com.dicoding.geotaggingjbg.ui.landing.LandingActivity
import com.dicoding.geotaggingjbg.ui.utils.ResultState

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun setupAction() {
        val factory: LoginViewModelFactory = LoginViewModelFactory.getInstance(this.applicationContext)
        val viewModel: LoginViewModel by viewModels { factory }
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            Log.d("isi data login", "$email, $password")

            viewModel.login(email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showLoading(false)
                            val currentTime = System.currentTimeMillis() // Mendapatkan waktu saat ini dalam milidetik
                            val expireTime = currentTime + 3 * 24 * 60 * 60 * 1000 // Tambahkan 3 hari dalam milidetik
                            viewModel.saveSession(
                                UserModel(email, result.data.token, true, result.data.uid, expireTime)
                            )
                            Log.d("TOKEN LOGIN", result.data.token)
                            showToast(getString(R.string.login_success))
                            startActivity(Intent(this, LandingActivity::class.java))
                        }

                        is ResultState.Error -> {
                            showLoading(false)
                            val message = when (val errorResponse = result.error) {
                                "\"email\" is not allowed to be empty" -> {
                                    getString(R.string.email_null_validation)
                                }

                                "\"email\" must be a valid email" -> {
                                    getString(R.string.email_valid_validaiton)
                                }

                                "\"password\" is not allowed to be empty" -> {
                                    getString(R.string.password_null_validation)
                                }

                                "Password must be at least 8 characters long" -> {
                                    getString(R.string.password_8_char_validation)
                                }

                                else -> {
                                    errorResponse
                                }
                            }

                            showAlert(
                                false, "Oops !!", message,
                                getString(R.string.back)
                            )
                        }
                    }
                }
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showAlert(condition: Boolean, tittle: String, message: String, buttonText: String) {
        AlertDialog.Builder(this).apply {
            setTitle(tittle)
            setMessage(message)
            setPositiveButton(buttonText) { _, _ ->
                if (condition) finish()
            }
            create()
            show()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}