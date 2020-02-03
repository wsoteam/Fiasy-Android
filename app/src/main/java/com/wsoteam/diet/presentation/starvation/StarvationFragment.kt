package com.wsoteam.diet.presentation.starvation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wsoteam.diet.R
import com.wsoteam.diet.Sync.WorkWithFirebaseDB
import java.util.*


class StarvationFragment : Fragment(R.layout.fragment_starvation) {

    companion object{

            const val STARVATION_HOURS = 16

        fun setTimestamp(millis: Long){
            (StarvationViewModel.getStarvation() as MutableLiveData).value?.timestamp = millis
            WorkWithFirebaseDB.setStarvationTimestamp(millis)
        }

        fun deleteStarvation(){
            (StarvationViewModel.getStarvation() as MutableLiveData).value = Starvation()
            WorkWithFirebaseDB.deleteStarvation()
        }

    }

    private lateinit var database: DatabaseReference
    private var tagCurrentState = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("USER_LIST")

        database.child(FirebaseAuth.getInstance().currentUser!!.uid).child("starvation").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.getValue(Starvation::class.java)?.apply { (StarvationViewModel.getStarvation() as MutableLiveData).value = this }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragment(StateNotStarted())
        StarvationViewModel.getStarvation().observe(this, Observer {

            val currentDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            startDate.timeInMillis = it.timestamp

            when {
                it.timestamp < 0 -> setFragment(StateNotStarted())
                currentDate.before(startDate) -> setFragment(StateTimerBeforeStarted())
                else -> setFragment(StateStarted())
            }
        })
    }

    private fun setFragment(fragment: Fragment){

        if (tagCurrentState != fragment::class.java.simpleName) {
            childFragmentManager
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.stateContainer, fragment, fragment::class.java.simpleName)
                    .commit()
            tagCurrentState = fragment::class.java.simpleName
        }
    }

}
