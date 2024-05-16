package com.dream.friend.ui.bottomsheet.realtimeImage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.dream.friend.R
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.interfaces.PhotoVerificationListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class RealTimeImageFailedBottomSheetDialog(var imageLink:String,var listener:PhotoVerificationListener): BottomSheetDialogFragment(){

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
        val view1 = inflater.inflate(R.layout.bottomsheet_real_time_image_failed_dialog, container, false)
        views = view1
        loadingDialog = requireActivity().dialogLoading()
        loadingDialog.dismiss()
        Glide.with(requireActivity())
            .load(imageLink)
            .placeholder(R.drawable.drawable_cr9dot5cffff8960c12ffff62a5)
            .error(R.drawable.drawable_bg_cr12cfff7f6)
            .into(views.findViewById<ShapeableImageView>(R.id.fImage))
        views.findViewById<MaterialButton>(R.id.btnTakePhoto).setOnClickListener{
            listener.openCamera()
        }
        views.findViewById<TextView>(R.id.btnLater).setOnClickListener{
           dismiss()
        }
        views.findViewById<ImageView>(R.id.ivCross).setOnClickListener{
            dismiss()
        }
        return view1
    }
}