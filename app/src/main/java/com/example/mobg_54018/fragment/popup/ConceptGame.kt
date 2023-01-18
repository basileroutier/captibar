/*
 * Copyright 2019, The Android Open Source Project
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

package com.example.mobg_54018.fragment.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mobg_54018.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/* This class is a fragment that displays a popup window with a concept game */
class ConceptGame : BottomSheetDialogFragment() {


    /**
     * This function is called when the fragment is created. It inflates the layout of the fragment and
     * returns the view
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle object containing the activity's previously saved state. If
     * the activity has never existed before, the value of the Bundle object is null.
     * @return The view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.popup_concept_game, container, false)
    }



}
