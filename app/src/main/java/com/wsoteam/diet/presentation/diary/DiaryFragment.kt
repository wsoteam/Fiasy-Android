package com.wsoteam.diet.presentation.diary

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.wsoteam.diet.AmplitudaEvents
import com.wsoteam.diet.Authenticate.POJO.Box
import com.wsoteam.diet.Config
import com.wsoteam.diet.InApp.ActivitySubscription
import com.wsoteam.diet.R
import com.wsoteam.diet.Sync.WorkWithFirebaseDB
import com.wsoteam.diet.common.Analytics.EventProperties
import com.wsoteam.diet.presentation.fab.MainFabMenu
import com.wsoteam.diet.presentation.diary.DiaryFragment.Companion.PremiumState.Hiden
import com.wsoteam.diet.presentation.diary.DiaryFragment.Companion.PremiumState.Revealed
import com.wsoteam.diet.presentation.diary.DiaryViewModel.DiaryDay
import com.wsoteam.diet.presentation.fab.FabMenuViewModel
import com.wsoteam.diet.utils.FiasyDateUtils
import com.wsoteam.diet.utils.ImageSpan
import com.wsoteam.diet.utils.RichTextUtils.replaceWithIcon
import com.wsoteam.diet.utils.dp
import com.wsoteam.diet.utils.getVectorIcon
import com.wsoteam.diet.views.CompactCalendarView
import com.wsoteam.diet.views.CompactCalendarView.CompactCalendarViewListener
import com.wsoteam.diet.views.GuardNestedScrollView
import java.text.SimpleDateFormat
import java.util.*

class DiaryFragment : Fragment() {

    companion object {
        private val formatterFullStyle = SimpleDateFormat("LLLL, EEEE dd", Locale.getDefault())
        private val formatterMonth = SimpleDateFormat("LLLL", Locale.getDefault())

        private enum class PremiumState {
            Revealed,
            Hiden
        }
    }

    private val calendar = Calendar.getInstance()
    protected val targets = hashMapOf<View, Target>()

    private lateinit var premiumContainer: View

    private lateinit var root: GuardNestedScrollView
    private lateinit var toolbar: Toolbar
    private lateinit var container: RecyclerView
    private lateinit var calendarView: CompactCalendarView
    private lateinit var smallCalendarView: View
    private val dragHelper by lazy {
        ViewDragHelper.create(toolbar.parent as ViewGroup,
                object : ViewDragHelper.Callback() {
                    override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                        return child.id == R.id.drag_controller
                    }

                    override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
                        super.onViewCaptured(capturedChild, activePointerId)

                        container.requestDisallowInterceptTouchEvent(true)
                    }

                    override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                        super.onViewReleased(releasedChild, xvel, yvel)

                        container.requestDisallowInterceptTouchEvent(false)

                        val translated = Math.abs(container.translationY)
                        val hMax = (toolbar.parent as View).height - toolbar.height

                        isCalendarExpanded = translated < hMax * 0.5f

                        toggleCalendar()
                    }

                    override fun getViewHorizontalDragRange(child: View): Int = 0
                    override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int =
                            child.left

                    override fun getViewVerticalDragRange(child: View): Int {
                        return ((toolbar.parent as View).height - toolbar.height)
                    }

                    override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                        return top.coerceIn(toolbar.height - child.height, child.top)
                    }

                    override fun onViewPositionChanged(changedView: View, left: Int, top: Int,
                                                       dx: Int, dy: Int) {

                        container.translationY += dy
                    }
                })
    }

    private var isCalendarExpanded = false

    override fun onCreateView(inflater: LayoutInflater,
                              parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_diary, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkWithFirebaseDB.setFirebaseStateListener()

        root = view.findViewById(R.id.root)

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setOnClickListener { toggleCalendar() }

        val p = toolbar.parent as View
        p.setOnTouchListener { v, event ->
            dragHelper.processTouchEvent(event)
            dragHelper.capturedView != null
        }

        premiumContainer = view.findViewById<View>(R.id.premium_banner_root)

        smallCalendarView = toolbar.findViewById(R.id.small_calendar)
        smallCalendarView.visibility = View.GONE
        smallCalendarView.setOnClickListener {
            val date = Calendar.getInstance()
            calendarView.setCurrentDate(date.time)

            DiaryViewModel.currentDate = DiaryDay(
                    date[Calendar.DAY_OF_MONTH],
                    date[Calendar.MONTH],
                    date[Calendar.YEAR]
            )

            updateTitle()
        }

        container = view.findViewById(R.id.container)

        container.addItemDecoration(object : ItemDecoration() {
            val spaceHeight = dp(requireContext(), 12f)

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                super.getItemOffsets(outRect, view, parent, state)

                val wa = parent.adapter as WidgetsAdapter
                if (parent.getChildAdapterPosition(view) != wa.indexOf(R.layout.ms_item_water_list)) {
                    outRect.bottom = spaceHeight
                }
            }
        })
        container.adapter = WidgetsAdapter()

        calendarView = view.findViewById(R.id.calendar)
        calendarView.setListener(object : CompactCalendarViewListener {
            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.MONTH, firstDayOfNewMonth.month)

                toolbar.title = formatterMonth.format(calendar.timeInMillis).capitalize()
                updateTitleExpandStateIcon()
            }

            override fun onDayClick(dateClicked: Date) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateClicked.time

                DiaryViewModel.currentDate = DiaryDay(
                        calendar[Calendar.DAY_OF_MONTH],
                        calendar[Calendar.MONTH],
                        calendar[Calendar.YEAR]
                )

                toggleCalendar()
            }
        })

        root.setOnScrollChangeListener(object : OnScrollChangeListener {
            private var currentState = Revealed

            override fun onScrollChange(v: NestedScrollView,
                                        scrollX: Int,
                                        scrollY: Int,
                                        oldScrollX: Int,
                                        oldScrollY: Int) {

                val scrollingDown = scrollY > oldScrollY
                val spaceLeft = premiumContainer.height + premiumContainer.translationY

                 FabMenuViewModel.fabState.value = if (scrollingDown)
                     FabMenuViewModel.FAB_HIDE else  FabMenuViewModel.FAB_SHOW


                currentState = if (spaceLeft == 0f) Hiden else Revealed

                var nextState = currentState

                if (scrollingDown && spaceLeft > 0f && !(spaceLeft == 1f * premiumContainer.height && scrollY > spaceLeft)) {
                    premiumContainer.translationY = -1f * Math.min(scrollY, premiumContainer.height)

                } else if (scrollY <= premiumContainer.height) {
                    premiumContainer.animate().cancel()

                    if (spaceLeft != 1f * premiumContainer.height) {
                        premiumContainer.translationY = -1f * Math.min(scrollY, premiumContainer.height)
                    }
                } else {
                    if (scrollingDown && spaceLeft != 0f) {
                        nextState = Hiden
                    }

                    if (!scrollingDown && spaceLeft == 0f) {
                        nextState = Revealed
                    }
                }

                if (nextState == currentState) {
                    return
                }

                premiumContainer.animate().cancel()

                val animator = premiumContainer.animate()

                if (nextState == Revealed) {
                    animator.translationY(0f)
                } else if (nextState == Hiden) {
                    animator.translationY(-1f * premiumContainer.height)
                }

                animator.duration = 120
                animator.withEndAction {
                    currentState = nextState
                }
                animator.start()

            }
        })

        calendarView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                calendarView.viewTreeObserver.removeOnPreDrawListener(this)

                smallCalendarView.translationX = 1f * toolbar.width - smallCalendarView.left

                val isPremium = requireContext()
                        .getSharedPreferences(Config.STATE_BILLING, Context.MODE_PRIVATE)
                        .getBoolean(Config.STATE_BILLING, false)

                var box = Box()
                box.comeFrom = AmplitudaEvents.view_prem_header
                box.buyFrom = EventProperties.trial_from_header
                box.isOpenFromIntrodaction = false
                box.isOpenFromPremPart = true
                premiumContainer.setOnClickListener {
                    startActivity(Intent(requireContext(), ActivitySubscription::class.java)
                            .putExtra(Config.TAG_BOX, box))
                }

                if (!isPremium) {
                    premiumContainer.visibility = View.VISIBLE

                    val target = object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                            premiumContainer.background = BitmapDrawable(premiumContainer.resources, bitmap)
                            targets.remove(premiumContainer)
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) = Unit
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
                    }

                    targets[premiumContainer] = target

                    if (premiumContainer.width > 0 && premiumContainer.height > 0) {
                        Picasso.get()
                                .load(R.drawable.banner_prem_main)
                                .resize(premiumContainer.width, premiumContainer.height)
                                .centerCrop()
                                .into(target)
                    }
                    view.findViewById<View>(R.id.gift_image).let { i ->
                        val giftTarget = object : Target {
                            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                                i.background = BitmapDrawable(premiumContainer.resources, bitmap)

                                targets.remove(i)
                            }

                            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) = Unit
                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
                        }

                        targets[i] = giftTarget

                        try {
                            Picasso.get()
                                    .load(R.drawable.star_1)
                                    .resize(premiumContainer.width, premiumContainer.height)
                                    .into(giftTarget)
                        }catch (e: java.lang.Exception){

                        }
                    }
                } else {
                    premiumContainer.visibility = View.GONE
                }

                val toolbarContainer = toolbar.parent as View
                (toolbarContainer.layoutParams as FrameLayout.LayoutParams).apply {
                    topMargin = premiumContainer.height
                }

                (container.layoutParams as FrameLayout.LayoutParams).apply {
                    topMargin = premiumContainer.height + toolbar.height
                }

                return false
            }
        })

        updateTitle()

        ViewCompat.setNestedScrollingEnabled(container, false)

        DiaryViewModel.scrollToPosition.observe(this, waterObserver)

    }


    private var oldStatusBarColor = 0

    private val waterObserver = androidx.lifecycle.Observer<Int> {position ->
        Log.d("kkk", "position = $position")
        if(position != null)
        container.post {
            val y = container.y + container.getChildAt(position).y - premiumContainer.height
            root.smoothScrollTo(0, y.toInt())
            DiaryViewModel.scrollToPosition.value = null
        }
    }

    override fun onResume() {
        super.onResume()

        oldStatusBarColor = activity?.window?.statusBarColor ?: 0

        if (oldStatusBarColor != 0) {
            activity?.window?.statusBarColor = 0xFF12061C.toInt()
        }

        val isPremium = requireContext()
                .getSharedPreferences(Config.STATE_BILLING, Context.MODE_PRIVATE)
                .getBoolean(Config.STATE_BILLING, false)

        premiumContainer.visibility = if (isPremium) View.GONE else View.VISIBLE

        container.translationY = 0f
    }

    override fun onPause() {
        super.onPause()

        if (oldStatusBarColor != 0) {
            activity?.window?.statusBarColor = oldStatusBarColor
        }

    }

    private fun toggleCalendar() {
        if (isCalendarExpanded) {
            container.animate()
                    .translationY(0f)
                    .setDuration(220)
                    .withEndAction {
                        val p = container
                        p.setPadding(0, 0, 0, 0)
                    }
                    .start()

            smallCalendarView.animate()
                    .translationX(1f * toolbar.width - smallCalendarView.left)
                    .setDuration(220)
                    .withEndAction() {
                        smallCalendarView.visibility = View.GONE
                    }
                    .start()
        } else {
            val date = DiaryViewModel.currentDate.calendar
            calendarView.setCurrentDate(date.time)

            container.animate()
                    .translationY(1f * (toolbar.parent as View).height - toolbar.height)
                    .setDuration(220)
                    .withEndAction {
                        val p = container
                        val extraPadding = (toolbar.parent as View).height - toolbar.height
                        p.setPadding(0, 0, 0, extraPadding)
                    }
                    .start()

            smallCalendarView.animate()
                    .translationX(0f)
                    .setDuration(220)
                    .withStartAction {
                        smallCalendarView.visibility = View.VISIBLE
                    }
                    .start()
        }

        isCalendarExpanded = !isCalendarExpanded

        if (!isCalendarExpanded) {
            updateTitle()
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.MONTH, DiaryViewModel.currentDate.month)

            toolbar.title = formatterMonth.format(calendar.timeInMillis).capitalize()
            updateTitleExpandStateIcon()
        }
    }

    private fun updateTitle() {
        val today = DiaryViewModel.isToday
        val yesterday = FiasyDateUtils.isYesterday(DiaryViewModel.currentDate)
        val tomorrow = FiasyDateUtils.isTomorrow(DiaryViewModel.currentDate)

        if (!isCalendarExpanded && (today || yesterday || tomorrow)) {
            toolbar.title = when {
                today -> getString(R.string.today)
                yesterday -> getString(R.string.yesterday)
                else -> getString(R.string.tomorrow)
            }
        } else {
            val target = DiaryViewModel.currentDate

            calendar.set(Calendar.MONTH, target.month)
            calendar.set(Calendar.YEAR, target.year)
            calendar.set(Calendar.DAY_OF_MONTH, target.day)

            toolbar.title = formatterFullStyle.format(calendar.timeInMillis).capitalize()
        }

        updateTitleExpandStateIcon()
    }

    private fun updateTitleExpandStateIcon() {
        val targetDrawable = if (!isCalendarExpanded)
            R.drawable.ic_arrow_drop_down_white_24dp
        else
            R.drawable.ic_arrow_drop_up_white_24dp

        val dropdownIcon = requireContext().getVectorIcon(targetDrawable)
        dropdownIcon.setBounds(0, 0, dp(requireContext(), 16f), dp(requireContext(), 16f))

        val dropdownSpan = ImageSpan(dropdownIcon, ImageSpan.ALIGN_BASELINE)
        toolbar.title = TextUtils.concat(toolbar.title, " ", " ".replaceWithIcon(dropdownSpan))
    }

}
