package com.example.pdf_scanner.ui.component.detail_text.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.EventOCRText
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

class OCRTextFragment(var e: EventOCRText, var imgBitmap: Bitmap): Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_ocr_text, container, false)


        var btnRecognize = root.findViewById<Button>(R.id.btnTextRecognize)
        btnRecognize.setOnClickListener {
            convertText(imgBitmap!!)
        }

        return root
    }

    private fun convertText(imageBitmap: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(imageBitmap)
        val languageIdentifier = FirebaseNaturalLanguage.getInstance()
            .languageIdentification
        val detector: FirebaseVisionTextRecognizer =
            FirebaseVision.getInstance().cloudTextRecognizer

        detector.processImage(image)
            .addOnSuccessListener(OnSuccessListener<FirebaseVisionText?> { firebaseVisionText ->
                processTxt(firebaseVisionText)
            }).addOnFailureListener(OnFailureListener { // handling an error listener.
                Log.e("addOnFailureListener", "OnFailureListener")
            })
    }

    private fun processTxt(text: FirebaseVisionText) {
        val blocks: List<FirebaseVisionText.TextBlock> = text.textBlocks

        if (blocks.isEmpty()) {
            // Toast.makeText(this@MainActivity, "No Text ", Toast.LENGTH_LONG).show()
            return
        }
        var result = ""
        for (block in blocks) {
            val txt: String = block.text
            result += txt + "\n"
            // textview.setText(txt)
        }
        Log.e("processTxt", result)
    }
}