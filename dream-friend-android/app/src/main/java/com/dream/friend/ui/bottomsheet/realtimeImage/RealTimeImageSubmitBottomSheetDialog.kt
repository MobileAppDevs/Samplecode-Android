package com.dream.friend.ui.bottomsheet.realtimeImage

import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.dream.friend.R
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.interfaces.PhotoVerificationListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class RealTimeImageSubmitBottomSheetDialog(var imgUri:Uri, var listener:PhotoVerificationListener): BottomSheetDialogFragment(){

    lateinit var views: View
    lateinit var loadingDialog: Dialog
    lateinit var btnTakePhoto:MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenBottomSheetDialog)
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view1 = inflater.inflate(R.layout.bottomsheet_real_time_image_submit_retake_dialog, container, false)
        views = view1
        loadingDialog = requireActivity().dialogLoading()
        loadingDialog.dismiss()
        views.findViewById<ImageView>(R.id.ivMyPhoto).setImageURI(imgUri)
        views.findViewById<TextView>(R.id.btnRetake).setOnClickListener{
            listener.openCamera()
        }
        views.findViewById<MaterialButton>(R.id.btnSubmit).setOnClickListener{
            listener.uploadPhoto()
        }
        views.findViewById<ImageView>(R.id.ivCross).setOnClickListener{
            dismiss()
        }
        return view1
    }
}