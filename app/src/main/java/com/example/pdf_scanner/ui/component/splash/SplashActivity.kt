package com.example.pdf_scanner.ui.component.splash

import android.Manifest
import android.animation.Animator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.databinding.ActivitySplashBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.component.history.HistoryActivity
import com.example.pdf_scanner.ui.component.main.MainActivity
import com.oneadx.vpnclient.utils.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding
    val viewModel: SplashViewModel by viewModels()
    var isStartCamera = false

    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSplashVersion.text = getVersion()
        binding.laAnimation.setAnimation(R.raw.splash_scanner)
        binding.laAnimation.playAnimation()
        binding.laAnimation.repeatCount = 1

        binding.laAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onAnimationEnd(animation: Animator?) {
                binding.laAnimation.cancelAnimation()
                changeAct()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeAct() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                );
            }
        }

        if (isStartCamera) {
            var intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            var intent = Intent(this@SplashActivity, HistoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getVersion(): String {
        return try {
            "v " + packageManager
                .getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "v 1.0.0"
        }
    }

    private fun observeStartCamera(isEnabled: Resource<Boolean>) {
        when (isEnabled) {
            is Resource.Success -> {
                isStartCamera = isEnabled.data!!
            }
        }
    }

    override fun observeViewModel() {
        observe(viewModel.liveStartedCamera, ::observeStartCamera)
    }
}