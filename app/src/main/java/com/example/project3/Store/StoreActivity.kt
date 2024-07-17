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
    private val sellItemList = mutableListOf<Item>() // 전체 아이템 리스트
    private val storeList = mutableListOf<Item>() // 판매리스트
    private val invenList = mutableListOf<Item>() // 보유리스트
    private val purchasedItemIds = mutableSetOf<Int>() // 보유 아이템 아이디 리스트
    private var equippedItemId: Int? = null // 착용 아이템 아이디

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        apiServer = ApiClient.apiService

        val storeRecyclerView: RecyclerView = findViewById(R.id.store_recyclerview)
        storeAdapter = StoreAdapter(storeList, purchasedItemIds, this)
        storeRecyclerView.adapter = storeAdapter
        storeRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val invenRecyclerView: RecyclerView = findViewById(R.id.inven_recyclerview)
        invenAdapter = InvenAdapter(invenList, equippedItemId, this) {
            updateMainUi() // Pass the function to update the main UI
        }
        invenRecyclerView.adapter = invenAdapter
        invenRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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
        apiServer.getAllReceipts().enqueue(object : Callback<List<Receipt>> {
            override fun onResponse(call: Call<List<Receipt>>, response: Response<List<Receipt>>) {
                if (response.isSuccessful) {
                    response.body()?.let { receipts ->
                        val userId = getUserId().toString()
                        val userReceipts = receipts.filter { it.user == userId }
                        purchasedItemIds.clear()
                        purchasedItemIds.addAll(userReceipts.map { it.item })
                        invenList.clear()
                        invenList.addAll(sellItemList.filter { it.item_id in purchasedItemIds })
                        updateItemLists()
                    }
                } else {
                    showError("Failed to load receipts1")
                }
            }

            override fun onFailure(call: Call<List<Receipt>>, t: Throwable) {
                showError("Failed to load receipts2: ${t.message}")
            }
        })
    }

    private fun updateItemLists() {
        storeList.clear()
        storeList.addAll(sellItemList.filter { it.item_id !in purchasedItemIds })
        storeAdapter.notifyDataSetChanged()
        invenAdapter.notifyDataSetChanged()
    }

    private fun getUserId(): String? {
        val returnId = UserHolder.getUser()?.userId
        return returnId
    }

    override fun onButtonClick(item: Item) {
        purchaseItem(item)
    }

    private fun purchaseItem(item: Item) {
        val receipt = Receipt(user = getUserId().toString(), item = item.item_id)
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
        val user = UserHolder.getUser()
        if (user != null) {
            user.characterName = item.item_name // Update the characterName
            UserHolder.setUser(user) // Update the UserHolder
        }
        invenAdapter.updateEquippedItem(equippedItemId)
        updateMainUi()
    }

    private fun updateMainUi() {
        // Your logic to update the main UI
        // This can include refreshing views, showing a toast, etc.
        Toast.makeText(this, "Character updated to ${UserHolder.getUser()?.characterName}", Toast.LENGTH_SHORT).show()
    }
}
