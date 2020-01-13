package com.wsoteam.diet.presentation.training.executor



import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.wsoteam.diet.R
import com.wsoteam.diet.presentation.training.TrainingDay
import com.wsoteam.diet.presentation.training.TrainingUid
import com.wsoteam.diet.presentation.training.TrainingViewModel
import kotlinx.android.synthetic.main.fragment_training_day_done.*
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import com.wsoteam.diet.utils.getBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class TrainingDayDoneFragment : Fragment(R.layout.fragment_training_day_done) {


    companion object{

        private const val TRAINING_DONE_BUNDLE_KEY = "TRAINING_DONE_BUNDLE_KEY"
        fun newInstance(day: TrainingDay?, trainingUid: String?) :TrainingDayDoneFragment{
            val fragment = TrainingDayDoneFragment()
            val bundle = Bundle()

            bundle.putParcelable(TRAINING_DONE_BUNDLE_KEY, day)
            bundle.putString(TrainingUid.training, trainingUid)

            fragment.arguments = bundle
            return fragment
        }
    }

    private var trainingDay: TrainingDay? = null
    private var trainingUid: String? = null

    private lateinit var  model: TrainingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        continueTraining.setOnClickListener {

        }
        repeatTraining.setOnClickListener {
            
        }

        shareTraining.setOnClickListener {
            shareImageFromURI(getUriFromBitmap(resultImg.getBitmap()))
        }

        model = ViewModelProviders.of(this)[TrainingViewModel::class.java]

        arguments?.apply {
            getParcelable<TrainingDay>(TRAINING_DONE_BUNDLE_KEY)?.apply {
                trainingDay = this
                updateUi(this)
            }

            getString(TrainingUid.training).apply {
                trainingUid = this
            }
        }
    }

    private fun updateUi(trainingDay: TrainingDay){
        day.text = trainingDay.day.toString()
        dayProgressBar.max = model.getTrainings().value?.trainings?.get(trainingUid)?.days?.size ?: 28
        dayProgressBar.progress = trainingDay.day ?: 1

        exercisesCount.text = (trainingDay.exercises?.size ?: 0).toString()

        time.text = "10:20"
    }


    private fun shareImageFromURI(uri: Uri?) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share Image"))

    }

    private fun getUriFromBitmap(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(activity?.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")

            val out = FileOutputStream(file)
            bmp?.compress(CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            bmpUri = Uri.fromFile(file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

}