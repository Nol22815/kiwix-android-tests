/*
 * Kiwix Android
 * Copyright (c) 2022 Kiwix <android.kiwix.org>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.kiwix.kiwixmobile.initial.download

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import applyWithViewHierarchyPrinting
import com.adevinta.android.barista.interaction.BaristaSleepInteractions
import com.adevinta.android.barista.interaction.BaristaSwipeRefreshInteractions.refresh
import junit.framework.AssertionFailedError
import org.kiwix.kiwixmobile.BaseRobot
import org.kiwix.kiwixmobile.Findable.StringId.TextId
import org.kiwix.kiwixmobile.Findable.Text
import org.kiwix.kiwixmobile.Findable.ViewId
import org.kiwix.kiwixmobile.R
import org.kiwix.kiwixmobile.core.utils.files.Log
import org.kiwix.kiwixmobile.testutils.TestUtils
import org.kiwix.kiwixmobile.testutils.TestUtils.testFlakyView
import org.kiwix.kiwixmobile.utils.RecyclerViewMatcher

fun initialDownload(func: InitialDownloadRobot.() -> Unit) =
  InitialDownloadRobot().applyWithViewHierarchyPrinting(func)

class InitialDownloadRobot : BaseRobot() {

  fun clickDownloadOnBottomNav() {
    clickOn(ViewId(R.id.downloadsFragment))
  }

  fun assertLibraryListDisplayed() {
    isVisible(ViewId(R.id.libraryList))
  }

  fun refreshOnlineList() {
    refresh(R.id.librarySwipeRefresh)
  }

  fun refreshLocalLibraryData() {
    try {
      refresh(R.id.zim_swiperefresh)
      pauseForBetterTestPerformance()
    } catch (e: RuntimeException) {
      Log.w("InitialDownloadTest", "Failed to refresh ZIM list: " + e.localizedMessage)
    }
  }

  fun waitForDataToLoad(retryCountForDataToLoad: Int = 10) {
    try {
      isVisible(TextId(R.string.your_languages))
    } catch (e: RuntimeException) {
      if (retryCountForDataToLoad > 0) {
        waitForDataToLoad(retryCountForDataToLoad - 1)
        return
      }
      // throw the exception when there is no more retry left.
      throw RuntimeException("Couldn't load the online library list.\n Original exception = $e")
    }
  }

  fun downloadZimFile() {
    pauseForBetterTestPerformance()
    testFlakyView({
      onView(
        RecyclerViewMatcher(R.id.libraryList).atPosition(
          1
        )
      ).perform(click())
    })
  }

  fun assertStorageConfigureDialogDisplayed() {
    testFlakyView({ isVisible(Text("Download book to internal storage?")) })
  }

  fun assertStopDownloadDialogDisplayed() {
    testFlakyView({ isVisible(Text("Stop download?")) })
  }

  fun clickOnYesToConfirm() {
    testFlakyView({ onView(withText("YES")).perform(click()) })
  }

  fun assertDownloadStart() {
    testFlakyView({ isVisible(ViewId(R.id.stop)) }, 10)
  }

  fun stopDownload() {
    testFlakyView({ onView(withId(R.id.stop)).perform(click()) })
  }

  fun assertDownloadStop() {
    try {
      onView(withId(R.id.stop)).check(doesNotExist())
    } catch (e: AssertionFailedError) {
      BaristaSleepInteractions.sleep(TestUtils.TEST_PAUSE_MS_FOR_DOWNLOAD_TEST.toLong())
      assertDownloadStop()
    }
  }

  private fun pauseForBetterTestPerformance() {
    BaristaSleepInteractions.sleep(TestUtils.TEST_PAUSE_MS.toLong())
  }

  fun stopDownloadIfAlreadyStarted() {
    try {
      pauseForBetterTestPerformance()
      onView(withId(R.id.stop)).check(matches(isDisplayed()))
      stopDownload()
      assertStopDownloadDialogDisplayed()
      clickOnYesToConfirm()
      pauseForBetterTestPerformance()
    } catch (e: Exception) {
      Log.i(
        "INITIAL_DOWNLOAD_TEST",
        "Failed to stop downloading. Probably because it is not downloading the zim file"
      )
    }
  }
}
