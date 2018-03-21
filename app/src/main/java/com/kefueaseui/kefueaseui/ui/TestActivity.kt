package com.kefueaseui.kefueaseui.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kefueaseui.kefueaseui.R
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.test_item.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.net.URL
import java.util.*

class TestActivity : Activity() {

    var TextView.text : CharSequence
    get() = getText()
    set(value) = setText(value)

    private val items = listOf(
            "Mon 6/23 - Sunny - 31/17",
            "Tue 6/24 - Foggy - 21/8",
            "Wed 6/25 - Cloudy - 22/17",
            "Thurs 6/26 - Rainy - 18/11",
            "Fri 6/27 - Foggy - 21/10",
            "Sat 6/28 - Rainy - 23/18",
            "Sun 6/29 - Sunny - 20/7"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        message.text = "Hello Kotlin!!!"
        initView()
        doAsync {
            Request("http://kefu.easemob.com/v1/Tenants/388/robots/visitor/greetings/app").run()
            uiThread { longToast("async") }
        }



    }

    fun initView(){
        val forecastList = findViewById(R.id.recycler_view) as RecyclerView
        forecastList.layoutManager = LinearLayoutManager(this)
        forecastList.adapter = TestAdapter(this,items)
        message.setOnClickListener({ message.text = "click"})
    }

    fun add(x:Int,y:Int = 1):Int{
        return x+y
    }




}

data class Forecast(val date: Date,val tem: Float, val details: String)

class Request(val url: String){
    fun run(){
        val forecastJsonStr = URL(url).readText()
        Log.e("run", forecastJsonStr)
    }
}

class TestAdapter(mContext: Context, val items:List<String>): RecyclerView.Adapter<TestAdapter.ViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder{
        return ViewHolder(inflater.inflate(R.layout.test_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tv_content.text = item.toString();
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_title:TextView = itemView.findViewById(R.id.tv) as TextView
        var tv_content:TextView = itemView.findViewById(R.id.tv1) as TextView


    }


}