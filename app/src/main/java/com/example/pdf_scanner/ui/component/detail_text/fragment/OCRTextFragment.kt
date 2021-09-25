package com.example.pdf_scanner.ui.component.detail_text.fragment

import android.animation.Animator
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.OCRListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


class OCRTextFragment(var path: String, var e: OCRListener) : Fragment() {

    lateinit var animOCR: LottieAnimationView
    lateinit var textOCR: TextView
    lateinit var layoutOCR: LinearLayout
    lateinit var btnRecognize: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_ocr_text, container, false)

        var file = File(genPath())

        animOCR = root.findViewById<LottieAnimationView>(R.id.animRecognizeText)
        animOCR.setAnimation(R.raw.recognize_text)

        textOCR = root.findViewById(R.id.txtOCR)
        layoutOCR = root.findViewById(R.id.layoutOCR)
        btnRecognize = root.findViewById(R.id.btnTextRecognize)

        textOCR.movementMethod = ScrollingMovementMethod()

        animOCR.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                DynamicToast.makeError(requireContext(), "Cannot convert text from this image!!!")
                    .show()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })

        var btnRecognize = root.findViewById<Button>(R.id.btnTextRecognize)
        btnRecognize.setOnClickListener {
            animOCR.repeatCount = 2
            animOCR.playAnimation()
            var file = File(path)
            if (file.exists()) {
                val bmOptions = BitmapFactory.Options()
                val imageBitmap = BitmapFactory.decodeFile(file.absolutePath, bmOptions)
                convertText(imageBitmap)
            }
            btnRecognize.isClickable = false
        }

        if (file.exists()) {
            animOCR.visibility = View.GONE
            btnRecognize.visibility = View.GONE

            val text = StringBuilder()

            try {
                val br = BufferedReader(FileReader(file))
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
                textOCR.text = text
            } catch (e: IOException) {
            }
        }
        return root
    }

    private fun genPath(): String {
        var rootPath = path
        var path = ""
        var pathName = ""
        for (i in rootPath.indices.reversed()) {
            if (rootPath[i] == '/') {
                pathName = rootPath.substring(i + 1, rootPath.length - 4)
                rootPath = rootPath.substring(0, i + 1)
                break
            }
        }
        path = rootPath + pathName
        return path
    }

    private fun convertText(imageBitmap: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(imageBitmap)
        val languageIdentifier = FirebaseNaturalLanguage.getInstance()
            .languageIdentification
        val detector: FirebaseVisionTextRecognizer =
            FirebaseVision.getInstance().onDeviceTextRecognizer;
        detector.processImage(image)
            .addOnSuccessListener(OnSuccessListener<FirebaseVisionText?> { firebaseVisionText ->
                processTxt(firebaseVisionText)
            }).addOnFailureListener(OnFailureListener { // handling an error listener.
                DynamicToast.makeError(requireContext(), "Cannot convert text from this image!!!")
                    .show()
            })
    }

    private fun processTxt(text: FirebaseVisionText) {
        val blocks: List<FirebaseVisionText.TextBlock> = text.textBlocks
        if (blocks.isEmpty()) {
            return
        }
        var result = ""
        for (block in blocks) {
            val txt: String = block.text
            result += txt + "\n"
        }
        layoutOCR.visibility = View.GONE
        btnRecognize.visibility = View.GONE
        textOCR.visibility = View.VISIBLE
        textOCR.text = result
        e.detectOCR(result)
    }
}