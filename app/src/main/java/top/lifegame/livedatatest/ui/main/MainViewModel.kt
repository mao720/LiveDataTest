package top.lifegame.livedatatest.ui.main

import androidx.lifecycle.ViewModel
import top.lifegame.livedatatest.CleanLiveData

class MainViewModel : ViewModel() {
    //默认粘性效果
//    val text: MutableLiveData<String> by lazy {
//        MutableLiveData<String>()
//    }
    //去除粘性效果
    val text: CleanLiveData<String> by lazy {
        CleanLiveData<String>()
    }
}