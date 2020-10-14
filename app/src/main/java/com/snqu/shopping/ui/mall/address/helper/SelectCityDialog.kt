package com.snqu.shopping.ui.mall.address.helper

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.util.os.DeviceUtil
import com.snqu.shopping.R
import com.snqu.shopping.data.mall.entity.address.AreaEntity
import com.snqu.shopping.data.mall.entity.address.ProvinceEntity
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.select_city_dialog.*


/**
 * desc:城市选择器
 * time: 2019/2/1
 * @author 银进
 */
class SelectCityDialog : DialogFragment() {
    var selectCityCallBack: SelectCityCallBack? = null

    //选中的地址
    private var areaEntity: AreaEntity? = null

    //上一个选择的省位置
    private var lastPositionProvince = -1

    //上一个选择的市位置
    private var lastPositionCity = -1

    //上一个选择的区位置
    private var lastPositionCountry = -1

    //选择省份的适配器
    private val selectProvinceAdapter by lazy {
        SelectProvinceAdapter().apply {
            setOnItemClickListener { adapter, _, position ->
                if (areaEntity?.provinceEntity?.id == (adapter.data[position] as ProvinceEntity).id) {
                    //相等的话就不从新刷新市数据了（前提是在设置了数据源的情况下）
                    if (selectCityAdapter.data.isEmpty()) {
                        selectCityAdapter.setNewData(AreaData.cityListMap[areaEntity?.provinceEntity?.id]
                                ?: arrayListOf())
                    }
                } else {
                    areaEntity?.provinceEntity = (adapter.data[position] as ProvinceEntity)
                    selectedId = areaEntity?.provinceEntity?.id
                    if (lastPositionProvince != -1) {
                        notifyItemChanged(lastPositionProvince)
                    }
                    notifyItemChanged(position)
                    lastPositionProvince = position
                    areaEntity?.cityEntity = null
                    areaEntity?.countyEntity = null
                    selectCityAdapter.setNewData(AreaData.cityListMap[areaEntity?.provinceEntity?.id]
                            ?: arrayListOf())

                }
                selectedCity()
            }
        }
    }

    //选择市的适配器
    private val selectCityAdapter by lazy {
        SelectCityAdapter().apply {
            setOnItemClickListener { adapter, _, position ->
                if (areaEntity?.cityEntity?.id == (adapter.data[position] as ProvinceEntity).id) {
                    //相等的话就不从新刷新区数据了（前提是在设置了数据源的情况下）
                    if (selectCountyAdapter.data.isEmpty()) {
                        selectCountyAdapter.setNewData(AreaData.countyListMap[areaEntity?.cityEntity?.id]
                                ?: arrayListOf())
                    }
                } else {
                    areaEntity?.cityEntity = (adapter.data[position] as ProvinceEntity)
                    selectedId = areaEntity?.cityEntity?.id
                    if (lastPositionCity != -1) {
                        notifyItemChanged(lastPositionCity)
                    }
                    notifyItemChanged(position)
                    lastPositionCity = position
                    areaEntity?.countyEntity = null
                    selectCountyAdapter.setNewData(AreaData.countyListMap[areaEntity?.cityEntity?.id]
                            ?: arrayListOf())
                }
                selectedCountry()
            }
        }
    }

    //选择区的适配器
    private val selectCountyAdapter by lazy {
        SelectCountyAdapter().apply {
            setOnItemClickListener { adapter, _, position ->
                if (lastPositionCountry != -1) {
                    notifyItemChanged(lastPositionCountry)

                }
                notifyItemChanged(position)
                lastPositionCountry = position
                areaEntity?.countyEntity = (adapter.data[position] as ProvinceEntity)
                selectedId = areaEntity?.countyEntity?.id
                selectedCountry()
                selectCityCallBack?.selected(areaEntity)
                dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }

    //选择的title 0->省；1->市；2->区
    private var selectItem = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.select_city_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * 初始化View
     */
    private fun initView() {
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        areaEntity = arguments?.getParcelable(EXTRA_ADDRESS_ENTITY)
        if (areaEntity == null) {
            areaEntity = AreaEntity()
        }
        recycler_view_province.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = selectProvinceAdapter
        }
        recycler_view_city.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = selectCityAdapter
        }
        recycler_view_county.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = selectCountyAdapter
        }
        selectProvinceAdapter.selectedId = areaEntity?.provinceEntity?.id
        selectCityAdapter.selectedId = areaEntity?.cityEntity?.id
        selectCountyAdapter.selectedId = areaEntity?.countyEntity?.id
        selectProvinceAdapter.setNewData(AreaData.provinceEntities)
        //获取默认选中的位置以便后面刷新好用
        for (i in 0 until AreaData.provinceEntities.size) {
            if (areaEntity?.provinceEntity?.id == AreaData.provinceEntities[i].id) {
                lastPositionProvince = i
                break
            }
        }
        if (areaEntity?.provinceEntity != null) {
            //前一个的id获取下一个的数据
            val cityList = (AreaData.cityListMap[areaEntity?.provinceEntity?.id]
                    ?: arrayListOf())
            //获取默认选中的位置以便后面刷新好用
            selectCityAdapter.setNewData(cityList)
            for (i in 0 until cityList.size) {
                if (areaEntity?.cityEntity?.id == cityList[i].id) {
                    lastPositionCity = i
                    break
                }
            }
        }
        if (areaEntity?.cityEntity != null) {
            //前一个的id获取下一个的数据
            val countyList = (AreaData.countyListMap[areaEntity?.cityEntity?.id]
                    ?: arrayListOf())
            selectCountyAdapter.setNewData(countyList)
            //获取默认选中的位置以便后面刷新好用
            for (i in 0 until countyList.size) {
                if (areaEntity?.countyEntity?.id == countyList[i].id) {
                    lastPositionCountry = i
                    break
                }
            }
        }
        selectedProvince()
        smoothToOther()
        tv_province.onClick {
            selectedProvince()
        }
        tv_city.onClick {
            selectedCity()
        }
        tv_country.onClick {
            selectedCountry()
        }
        img_close.onClick {
            dismiss()
        }
    }

    /**
     * 选择区
     * 说明已经存在省市了
     */
    private fun selectedCountry() {
        selectItem = 2
        recycler_view_province.visibility = View.GONE
        recycler_view_city.visibility = View.GONE
        recycler_view_county.visibility = View.VISIBLE
        val data = selectCountyAdapter.data
        if (data.isNotEmpty() && areaEntity?.countyEntity != null) {
            val indexPosition = data.indexOf(areaEntity?.countyEntity)
            if (indexPosition >= 0) {
                recycler_view_county.scrollToPosition(indexPosition)
            }
        }
        tv_province.setTextColor(Color.parseColor("#FF131413"))
        tv_province.text = areaEntity?.provinceEntity?.name ?: "请选择"

        tv_province.visibility = View.VISIBLE
        tv_city.setTextColor(Color.parseColor("#FF131413"))
        tv_city.text = areaEntity?.cityEntity?.name ?: "请选择"
        tv_city.visibility = View.VISIBLE
        tv_country.setTextColor(Color.parseColor("#FFFF8202"))
        tv_country.text = areaEntity?.countyEntity?.name ?: "请选择"
        tv_country.visibility = View.VISIBLE
        smoothToOther()
    }

    /**
     * 选择市
     * 说明已经存在省了
     */
    private fun selectedCity() {
        selectItem = 1
        recycler_view_province.visibility = View.GONE
        recycler_view_city.visibility = View.VISIBLE
        recycler_view_county.visibility = View.GONE
        val data = selectCityAdapter.data
        if (data.isNotEmpty() && areaEntity?.cityEntity != null) {
            val indexPosition = data.indexOf(areaEntity?.cityEntity)
            if (indexPosition >= 0) {
                recycler_view_city.scrollToPosition(indexPosition)
            }
        }
        tv_province.setTextColor(Color.parseColor("#FF131413"))
        tv_province.text = areaEntity?.provinceEntity?.name ?: "请选择"
        tv_province.visibility = View.VISIBLE
        tv_city.setTextColor(Color.parseColor("#FFFF8202"))
        tv_city.text = areaEntity?.cityEntity?.name ?: "请选择"
        tv_city.visibility = View.VISIBLE
        if (areaEntity?.cityEntity != null) {
            tv_country.setTextColor(Color.parseColor("#FF131413"))
            tv_country.text = areaEntity?.countyEntity?.name ?: "请选择"
            tv_country.visibility = View.VISIBLE
        } else {
            tv_country.visibility = View.GONE
        }
        smoothToOther()
    }

    /**
     * 选择省
     */
    private fun selectedProvince() {
        selectItem = 0
        recycler_view_province.visibility = View.VISIBLE
        recycler_view_city.visibility = View.GONE
        recycler_view_county.visibility = View.GONE
        val data = selectProvinceAdapter.data
        if (data.isNotEmpty() && areaEntity?.provinceEntity != null) {
            val indexPosition = data.indexOf(areaEntity?.provinceEntity)
            if (indexPosition >= 0) {
                recycler_view_province.scrollToPosition(indexPosition)
            }
        }
        tv_province.setTextColor(Color.parseColor("#FFFF8202"))
        tv_province.text = areaEntity?.provinceEntity?.name ?: "请选择"
        tv_province.visibility = View.VISIBLE
        if (areaEntity?.provinceEntity != null) {
            tv_city.setTextColor(Color.parseColor("#FF131413"))
            tv_city.text = areaEntity?.cityEntity?.name ?: "请选择"
            tv_city.visibility = View.VISIBLE
            if (areaEntity?.cityEntity != null) {
                tv_country.setTextColor(Color.parseColor("#FF131413"))
                tv_country.text = areaEntity?.countyEntity?.name ?: "请选择"
                tv_country.visibility = View.VISIBLE
            } else {
                tv_country.visibility = View.GONE
            }
        } else {
            //没有市那么就没有区
            tv_city.visibility = View.GONE
            tv_country.visibility = View.GONE
        }
        smoothToOther()
    }

    private fun smoothToOther() {
        when (selectItem) {
            0 -> {
                tv_province.post {
                    view_line.animate().translationX((tv_province.width - view_line.width) / 2f + DeviceUtil.dip2px(activity, 8f)).setDuration(500).start()
                }
            }
            1 -> {
                tv_city.post {
                    view_line.animate().translationX(tv_province.width + (tv_city.width - view_line.width) / 2f + DeviceUtil.dip2px(activity, 16f)).start()
                }
            }
            2 -> {
                tv_country.post {
                    view_line.animate().translationX(tv_province.width + tv_city.width + (tv_country.width - view_line.width) / 2f + DeviceUtil.dip2px(activity, 24f)).setDuration(500).start()

                }
            }
        }
    }

    companion object {
        const val EXTRA_ADDRESS_ENTITY = "EXTRA_ADDRESS_ENTITY"
    }

}
