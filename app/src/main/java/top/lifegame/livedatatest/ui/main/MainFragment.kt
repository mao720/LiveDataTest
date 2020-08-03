package top.lifegame.livedatatest.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import top.lifegame.livedatatest.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this.activity!!)[MainViewModel::class.java]
        val text1 = "初始文字被改变"
        val message = requireView().findViewById<TextView>(R.id.message)
        message.setOnClickListener {
            //未使用LiveData
//            message.text = text1
//            message.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            //使用LiveData
            viewModel.text.value = text1
        }
        //使用LiveData
        viewModel.text.observe(viewLifecycleOwner,
            Observer {
                message.text = it
                message.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            })
    }

}