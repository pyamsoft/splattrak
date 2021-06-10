/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.splattrak.ui

import android.view.View
import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pydroid.arch.ViewBinder

/** Call the ViewBinder.teardown() on a view holder at given index */
fun RecyclerView.teardownViewHolderAt(index: Int) {
    val holder = this.findViewHolderForAdapterPosition(index)
    if (holder is ViewBinder<*>) {
        holder.teardown()
    }
}

/** Call the ViewBinder.teardown() on a view holder for a given child view */
fun RecyclerView.teardownChildViewHolder(child: View) {
    val holder = this.getChildViewHolder(child)
    if (holder is ViewBinder<*>) {
        holder.teardown()
    }
}

/** Watches for RecyclerView child events */
fun interface RecyclerViewChildRemovedRegistration {

  fun unregister()
}

/** Watch a RecyclerView and react when children are removed from it */
@CheckResult
inline fun RecyclerView.Adapter<*>.doOnChildRemoved(
    crossinline block: (index: Int) -> Unit
): RecyclerViewChildRemovedRegistration {
  val observer =
      object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
          for (index in positionStart until positionStart + itemCount) {
            block(index)
          }
        }
      }

  this.registerAdapterDataObserver(observer)
  return RecyclerViewChildRemovedRegistration { this.unregisterAdapterDataObserver(observer) }
}
