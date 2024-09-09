package com.app.rupyz.generic.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.*

class SpeechToTextUtility(private val activity: FragmentActivity) {
	
	private var speechRecognizer: SpeechRecognizer? = null
	private lateinit var speechRecognizerIntent: Intent
	private var onResult: ((String) -> Unit)? = null
	private var isListening = false
	
	private  var permissionLauncher: ActivityResultLauncher<String>
	
	init {
		// Register the permission launcher
		permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
			if (isGranted) {
				showListeningDialog()
			} else {
				Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
			}
		}
	}
	
	// Function to initialize and start the process
	fun startSpeechToText(onResult: (String) -> Unit) {
		this.onResult = onResult
		
		if (!isPermissionGranted()) {
			requestPermission()
		} else {
			showListeningDialog()
		}
	}
	
	// Check if audio recording permission is granted
	private fun isPermissionGranted(): Boolean {
		return ContextCompat.checkSelfPermission(
				activity, Manifest.permission.RECORD_AUDIO
		                                        ) == PackageManager.PERMISSION_GRANTED
	}
	
	// Request audio recording permission using ActivityResultLauncher
	private fun requestPermission() {
		permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
	}
	
	// Set up and start listening
	private fun showListeningDialog() {
		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
		speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
			putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
			putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
		}
		
		val builder = AlertDialog.Builder(activity)
		builder.setTitle("Listening...")
		builder.setMessage("Please speak clearly.")
		builder.setCancelable(false)
		val dialog = builder.create()
		
		speechRecognizer?.setRecognitionListener(object : RecognitionListener {
			override fun onReadyForSpeech(params: Bundle?) {}
			
			override fun onBeginningOfSpeech() {}
			
			override fun onRmsChanged(rmsdB: Float) {}
			
			override fun onBufferReceived(buffer: ByteArray?) {}
			
			override fun onEndOfSpeech() {
				isListening = false
				dialog.dismiss()
			}
			
			override fun onError(error: Int) {
				isListening = false
				dialog.dismiss()
				Log.i(javaClass.name, "onError: $error")
				Toast.makeText(activity, "Error occurred while recognizing speech", Toast.LENGTH_SHORT).show()
			}
			
			override fun onResults(results: Bundle?) {
				isListening = false
				dialog.dismiss()
				val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
				data?.firstOrNull()?.let { onResult?.invoke(it) }
			}
			
			override fun onPartialResults(partialResults: Bundle?) {}
			
			override fun onEvent(eventType: Int, params: Bundle?) {}
		})
		
		isListening = true
		dialog.show()
		speechRecognizer?.startListening(speechRecognizerIntent)
	}
	
	// Clean up resources
	fun onDestroy() {
		speechRecognizer?.destroy()
	}
	
}
