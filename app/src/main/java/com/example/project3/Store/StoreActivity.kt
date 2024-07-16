package com.example.project3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.Class.Item
import com.example.project3.Class.Receipt
import com.example.project3.Store.InvenAdapter
import com.example.project3.Store.StoreAdapter
import com.google.android.gms.common.api.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreActivity : AppCompatActivity(), StoreAdapter.OnButtonClickListener, InvenAdapter.OnButtonClickListener {

    private lateinit var storeAdapter: StoreAdapter
    private lateinit var invenAdapter: InvenAdapter
    private lateinit var apiServer: APIServer
    private val sellItemList = mutableListOf<Item>()
    private val storeList = mutableListOf<Item>()
    private val invenList = mutableListOf<Item>()
    private val purchasedItemIds = mutableSetOf<Int>()
    private var equippedItemId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        apiServer = ApiClient.apiService

        val storeRecyclerView: RecyclerView = findViewById(R.id.store_recyclerview)
        storeAdapter = StoreAdapter(storeList, purchasedItemIds, this)
        storeRecyclerView.adapter = storeAdapter
        storeRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val invenRecyclerView: RecyclerView = findViewById(R.id.inven_recyclerview)
        invenAdapter = InvenAdapter(invenList, equippedItemId, this, )
        invenRecyclerView.adapter = invenAdapter
        invenRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)

        loadItems()
        loadReceipts()
    }

    private fun loadItems() {
        apiServer.getItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        sellItemList.clear()
                        sellItemList.addAll(it)
                        updateItemLists()
                    }
                } else {
                    showError("Failed to load items")
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                showError("Failed to load items: ${t.message}")
            }
        })
    }

    private fun loadReceipts() {
        val userId = getUserId().toString()
        apiServer.getUserReceipts(userId).enqueue(object : Callback<List<Receipt>> {
            override fun onResponse(call: Call<List<Receipt>>, response: Response<List<Receipt>>) {
                if (response.isSuccessful) {
                    response.body()?.let { receipts ->
                        purchasedItemIds.clear()
                        purchasedItemIds.addAll(receipts.map { it.item })
                        invenList.clear()
                        invenList.addAll(sellItemList.filter { it.item_id in purchasedItemIds })
                        updateItemLists()
                    }
                } else {
                    showError("Failed to load receipts")
                }
            }

            override fun onFailure(call: Call<List<Receipt>>, t: Throwable) {
                showError("Failed to load receipts: ${t.message}")
            }
        })
    }

    private fun updateItemLists() {
        storeList.clear()
        storeList.addAll(sellItemList.filter { it.item_id !in purchasedItemIds })
        storeAdapter.notifyDataSetChanged()
        invenAdapter.notifyDataSetChanged()
    }

    private fun getUserId(): Int {
        // 실제 유저 ID를 가져오는 로직으로 대체
        return 1
    }

    override fun onButtonClick(item: Item) {
        purchaseItem(item)
    }

    private fun purchaseItem(item: Item) {
        val receipt = Receipt(receiptId = 0, user = getUserId().toString(), item = item.item_id)
        apiServer.createReceipts(receipt).enqueue(object : Callback<Receipt> {
            override fun onResponse(call: Call<Receipt>, response: Response<Receipt>) {
                if (response.isSuccessful) {
                    loadReceipts()
                } else {
                    showError("Failed to purchase item")
                }
            }

            override fun onFailure(call: Call<Receipt>, t: Throwable) {
                showError("Failed to purchase item: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onEquipButtonClick(item: Item) {
        equippedItemId = item.item_id
        invenAdapter.updateEquippedItem(equippedItemId)
    }
}
