package com.example.pdf_scanner.ui.component.splash

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivitySplashBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.component.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var handler: Handler

    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSplashVersion.text = getVersion()

        binding.laAnimation.setAnimation(R.raw.splash_scanner)
        binding.laAnimation.playAnimation()

        handler = Handler()
        handler.postDelayed({
            // binding.rippleSplash.stopRippleAnimation()
            binding.laAnimation.cancelAnimation()
            var intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3500)
    }

    private fun getVersion(): String{
        return try {
            "v " + packageManager
                .getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "v 1.0.0"
        }
    }

    override fun observeViewModel() {

    }
}